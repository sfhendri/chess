package chess.allmoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;


public class BishopMoves implements allmoves {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currPosition) {
        int column = currPosition.getColumn();
        int row = currPosition.getRow();
        int[][] bishopMoveDirections = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};

        ChessGame.TeamColor team = board.getTeamOfSquare(currPosition);

        return allmoves.generateDirectionalMoves(board, currPosition, bishopMoveDirections, row, column, team);

    }
}
