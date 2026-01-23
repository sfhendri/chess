package chess.AllMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;


public class RookMoves implements AllMoves {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currPosition) {
        int column = currPosition.getColumn();
        int row = currPosition.getRow();
        int[][] Rook_Move_Directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        ChessGame.TeamColor team = board.getTeamOfSquare(currPosition);

        return AllMoves.generateDirectionalMoves(board, currPosition, Rook_Move_Directions, row, column, team);

    }
}

