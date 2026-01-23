package chess.AllMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public interface AllMoves {

    static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currPosition) {
        return null;
    }

    static boolean SquareValid(ChessPosition position) {
        return (position.getRow() >= 1 && position.getRow() <= 8) &&
                (position.getColumn() >= 1 && position.getColumn() <= 8);
    }

    // This function generates all moves that are static (Not directional)
    static HashSet<ChessMove> generateStaticMoves(ChessPosition currPosition, int[][] relativeMoves, ChessBoard board) {
        HashSet<ChessMove> moves = HashSet.newHashSet(8); //8 is the max number of moves of a Knight (the most static moves)

        int column = currPosition.getColumn();
        int row = currPosition.getRow();

        ChessGame.TeamColor team = board.getTeamOfSquare(currPosition);
        for (int[] relativeMove : relativeMoves) {
            ChessPosition possiblePosition = new ChessPosition(row + relativeMove[1], column + relativeMove[0]);
            if (AllMoves.SquareValid(possiblePosition) && board.getTeamOfSquare(possiblePosition) != team)
                moves.add(new ChessMove(currPosition, possiblePosition, null));
        }
        return moves;
    }

    static HashSet<ChessMove> generateDirectionalMoves(ChessBoard board, ChessPosition currPosition, int[][] moveDirections, int currY, int currX, ChessGame.TeamColor team) {
        HashSet<ChessMove> moves = HashSet.newHashSet(27); //We use 27 because a queen can have 27 moves
        for (int[] direction : moveDirections) {
            boolean obstructed = false;
            int i = 1;
            while (!obstructed) {
                ChessPosition possiblePosition = new ChessPosition(currY + direction[1]*i, currX + direction[0]*i);
                if (!AllMoves.SquareValid(possiblePosition)) {
                    obstructed = true;
                }
                else if (board.getPiece(possiblePosition) == null) {
                    moves.add(new ChessMove(currPosition, possiblePosition, null));
                }
                else if (board.getTeamOfSquare(possiblePosition) != team) {
                    moves.add(new ChessMove(currPosition, possiblePosition, null));
                    obstructed = true;
                }
                else if (board.getTeamOfSquare(possiblePosition) == team) {
                    obstructed = true;
                }
                else {
                    obstructed = true;
                }
                i++;
            }
        }
        return moves;
    }
}

