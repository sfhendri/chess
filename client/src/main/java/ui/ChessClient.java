package ui;

import chess.ChessGame;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import service.ServerFacade;
import utilities.StringUtilities;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static ui.EscapeSequences.*;

public class ChessClient {
    final private ServerFacade server;
    private State userState = State.LOGGED_OUT;
    private String authToken;
    private GameData gameData;
    private List<GameData> games = new ArrayList<>();

    public ChessClient() throws Exception {
        server = new ServerFacade("http://localhost:8080");
    }

    public void run() {
        System.out.println("👑 Welcome to 240 chess. Type Help to get started. 👑");
        Scanner scanner = new Scanner(System.in);

        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String input = scanner.nextLine();

            try {
                result = eval(input);
                System.out.printf("%s%s\n", RESET_TEXT_COLOR, result);
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }
        }

    }

    private void printPrompt() {

    }

    private String eval(String input) {
        var result = "Error with command. Try: Help";
        try {
            input = input.toLowerCase();
            var tokens = input.split(" ");
            if (tokens.length == 0) {
                tokens = new String[]{"Help"};
            }

            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            try {
                result = (String) this.getClass().getDeclaredMethod(tokens[0], String[].class).invoke(this, new Object[]{params});
            } catch (NoSuchMethodException e) {
                result = String.format("Unknown command\n%s", help(params));
            }
        } catch (InvocationTargetException e) {
            result = e.getCause().getMessage();
        } catch (Throwable e) {
            result = e.getMessage();
        }
        return result;
    }


    private String help(String[] params) {
        return switch (userState) {
            case LOGGED_IN -> getHelp(LOGGEDINHELP);
            case OBSERVING -> getHelp(OBSERVINGHELP);
            case BLACK, WHITE -> getHelp(PLAYINGHELP);
            default -> getHelp(LOGGEDOUTHELP);
        };
    }

    @SuppressWarnings("unused")
    private String quit(String[] params) {
        return "quit";
    }

    @SuppressWarnings("unused")
    private String login(String[] params) throws Exception {
        if (userState != State.LOGGED_OUT) {
            return "Must be logged out";
        }
        var username = getStringParam("username", params, 0);
        var password = getStringParam("password", params, 1);

        AuthData authData = server.login(username, password);
        userState = State.LOGGED_IN;
        authToken = authData.authToken();
        return String.format("Logged in as %s", username);
    }

    private String register(String[] params) throws Exception {
        if (userState != State.LOGGED_OUT) {
            return "Must be logged out";
        }
        var username = getStringParam("username", params, 0);
        var password = getStringParam("password", params, 1);
        var email = getStringParam("email", params, 2);

        AuthData authData = server.register(username, password, email);
        userState = State.LOGGED_IN;
        authToken = authData.authToken();
        return String.format("Logged in as %s", username);
    }

    private String logout(String[] ignore) throws Exception {
        verifyAuth();

        server.logout(authToken);
        userState = State.LOGGED_OUT;
        authToken = null;
        return "Logged out";
    }

    private String create(String[] params) throws Exception {
        verifyAuth();
        var gameName = getStringParam("game name", params, 0);

        server.createGame(authToken, gameName);

        return String.format("Created %s", gameName);
    }

    private String list(String[] params) throws Exception {
        verifyAuth();

        var gameList = server.listGames(authToken);
        games = Arrays.stream(gameList).toList();

        if (!games.isEmpty()) {
            int pos = 1;
            StringBuilder buf = new StringBuilder("Games:\n———————————————————————————————\n");
            for (var game : games) {
                var gameText = String.format("%d. %s white:%s black:%s state: %s%n", pos,
                        game.gameName(), game.whiteUsername(), game.blackUsername(), game.state());
                buf.append(gameText);
                pos++;
            }
            return buf.toString();
        }

        return "No games. Perhaps you would like to create one?";
    }


    private String join(String[] params) throws Exception {
        verifyAuth();

        // Get the game from the last listed games
        var game = getGame(params, 0);

        // Get the color the player wants to join as
        var color = getColor(params, 1);

        if (isPlaying() || isObserving()) {
            throw new Exception("Already in game");
        }

        // Call the server facade to join the game and store the returned GameData
        this.gameData = server.joinGame(authToken, game.gameID(), color);

        // Set user state based on chosen color
        userState = (color == ChessGame.TeamColor.WHITE ? State.WHITE : State.BLACK);

        // Immediately draw the board from the perspective of the player
        printGame(color, null);

        return String.format("Joined %s as %s", game.gameName(), color);
    }


    private String observe(String[] params) throws Exception {
        verifyAuth();
        var game = getGame(params, 0);
        if (isPlaying() || isObserving()) {
            throw new Exception("Already in game");
        }

        this.gameData = new GameData(0, "", "", "", new ChessGame(), GameData.State.UNDECIDED);
        userState = State.OBSERVING;
        printGame();
        return String.format("Joined %d as observer", game.gameID());
    }


    private String redraw(String[] params) throws Exception {
        verifyAuth();
        if (!isPlaying() && !isObserving()) {
            throw new Exception("No game being played");
        }

        printGame();
        return "";
    }


    private String legal(String[] params) throws Exception {
        verifyAuth();
        if (!isPlaying() && !isObserving()) {
            throw new Exception("No game being played");
        }

        printGame();
        return "";
    }


    private String move(String[] params) throws Exception {
        verifyAuth();
        if (!isPlaying()) {
            throw new Exception("No game being played");
        }
        var move = getStringParam("move", params, 0);
        return String.format("move %s", move);
    }

    private String leave(String[] params) throws Exception {
        if (!isPlaying() && !isObserving()) {
            throw new Exception("No game being played");
        }

        userState = State.LOGGED_IN;
        gameData = null;
        return "Left game";
    }

    private String resign(String[] params) throws Exception {
        if (!isPlaying() && !isObserving()) {
            throw new Exception("No game being played");
        }

        userState = State.LOGGED_IN;
        gameData = null;
        return "Left game";
    }


    private record Help(String cmd, String description) {
    }

    static final List<Help> LOGGEDOUTHELP = List.of(
            new Help("register <USERNAME> <PASSWORD> <EMAIL>", "to create an account"),
            new Help("login <USERNAME> <PASSWORD>", "to play chess"),
            new Help("quit", "playing chess"),
            new Help("help", "with possible commands")
    );

    static final List<Help> LOGGEDINHELP = List.of(
            new Help("create <NAME>", "a game"),
            new Help("list", "games"),
            new Help("join <POSITION> [WHITE|BLACK]", "a game"),
            new Help("observe <ID>", "a game"),
            new Help("logout", "when you are done"),
            new Help("quit", "playing chess"),
            new Help("help", "with possible commands")
    );

    static final List<Help> OBSERVINGHELP = List.of(
            new Help("legal", "moves for the current board"),
            new Help("redraw", "the board"),
            new Help("leave", "the game"),
            new Help("quit", "playing chess"),
            new Help("help", "with possible commands")
    );

    static final List<Help> PLAYINGHELP = List.of(
            new Help("redraw", "the board"),
            new Help("leave", "the game"),
            new Help("move <crcr> [q|r|b|n]", "a piece with optional promotion"),
            new Help("resign", "the game without leaving it"),
            new Help("legal <cr>", "moves for piece"),
            new Help("quit", "playing chess"),
            new Help("help", "with possible commands")
    );

    private String getHelp(List<Help> help) {
        StringBuilder sb = new StringBuilder();
        for (var me : help) {
            sb.append(String.format("  %s%s%s - %s%s%s\n", SET_TEXT_COLOR_BLUE,
                    me.cmd, RESET_TEXT_COLOR, SET_TEXT_COLOR_MAGENTA, me.description, RESET_TEXT_COLOR));
        }
        return sb.toString();

    }

    private void verifyAuth() throws Exception {
        if (userState == State.LOGGED_OUT || authToken == null) {
            throw new Exception("Please login or register");
        }
    }

    public boolean isPlaying() {
        return (gameData != null && (userState == State.WHITE || userState == State.BLACK) && !isGameOver());
    }


    public boolean isObserving() {
        return (gameData != null && (userState == State.OBSERVING));
    }

    public boolean isGameOver() {
        return (gameData != null && gameData.isGameOver());
    }

    public boolean isTurn() {
        return (isPlaying() && userState.isTurn(gameData.game().getTeamTurn()));
    }

    private void printGame() {
        var color = userState == State.BLACK ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        printGame(color, null);
    }

    private void printGame(ChessGame.TeamColor color, Collection<ChessPosition> highlights) {
        System.out.println("\n");
        System.out.print((gameData.game().getBoard()).toString(color, highlights));
        System.out.println();
    }

    private String getStringParam(String name, String[] params, int pos) throws Exception {
        if (params.length <= pos) {
            throw new Exception(String.format("Missing %s parameter", name));
        }
        return params[pos];
    }

    private int getIntParam(String[] params) throws Exception {
        if (params.length == 0) {
            throw new Exception(String.format("Missing %s parameter", "game Pos"));
        }

        var result = StringUtilities.tryParseInt(params[0]);
        if (result.isEmpty()) {
            throw new Exception(String.format("Parameter %s is not an int", "game Pos"));
        }
        return result.getAsInt();
    }

    private GameData getGame(String[] params, int pos) throws Exception {
        var gamePos = getIntParam(params) - 1;
        if (gamePos >= 0 && gamePos >= games.size()) {
            throw new Exception("invalid game requested");
        }

        return games.get(gamePos);
    }

    private ChessGame.TeamColor getColor(String[] params, int pos) throws Exception {
        if (params.length <= pos || params[pos] == null || params[pos].isBlank()) {
            throw new Exception("Missing color parameter (must be WHITE or BLACK)");
        }

        var colorText = params[pos].toUpperCase();
        if (!colorText.equals("WHITE") && !colorText.equals("BLACK")) {
            throw new Exception("Color must be WHITE or BLACK");
        }
        return ChessGame.TeamColor.valueOf(colorText);
    }

    @SuppressWarnings("unused")
    private void markMethodsUsed() throws Exception {
        // Dummy calls so static analysis sees them being used
        register(new String[]{});
        join(new String[]{});
        list(new String[]{});
        observe(new String[]{});
        leave(new String[]{});
        legal(new String[]{});
        redraw(new String[]{});
        resign(new String[]{});
        move(new String[]{});
        quit(new String[]{});
    }
}



