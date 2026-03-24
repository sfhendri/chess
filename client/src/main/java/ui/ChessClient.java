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

    private void printPrompt() {
    }

}
