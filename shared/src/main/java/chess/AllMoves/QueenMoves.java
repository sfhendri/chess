package chess.AllMoves;


import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;


public class QueenMoves implements AllMoves {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currPosition) {
        int column = currPosition.getColumn();
        int row = currPosition.getRow();
        int[][] Queen_Move_Directions = {{-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}};

        ChessGame.TeamColor team = board.getTeamOfSquare(currPosition);

        return AllMoves.generateDirectionalMoves(board, currPosition, Queen_Move_Directions, row, column, team);

    }
}
