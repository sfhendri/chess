package chess.allmoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;


public class RookMoves implements allmoves {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currPosition) {
        int column = currPosition.getColumn();
        int row = currPosition.getRow();
        int[][] RookMoveDirections = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        ChessGame.TeamColor team = board.getTeamOfSquare(currPosition);

        return allmoves.generateDirectionalMoves(board, currPosition, RookMoveDirections, row, column, team);

    }
}

