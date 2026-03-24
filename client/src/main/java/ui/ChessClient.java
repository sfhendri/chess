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


}
