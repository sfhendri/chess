package chess.AllMoves;

import chess.*;

import java.util.HashSet;

public class PawnMoves implements AllMoves {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currPosition) {
        HashSet<ChessMove> moves = HashSet.newHashSet(16); //16 is the max number of moves of a Pawn
        int column = currPosition.getColumn();
        int row = currPosition.getRow();
        ChessPiece.PieceType[] promotionPieces = new ChessPiece.PieceType[]{null};

        ChessGame.TeamColor team = board.getTeamOfSquare(currPosition);
        int moveIncrement = team == ChessGame.TeamColor.WHITE ? 1 : -1;

        boolean promote = (team == ChessGame.TeamColor.WHITE && row == 7) || (team == ChessGame.TeamColor.BLACK && row == 2);
        if (promote) {
            promotionPieces = new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN};
        }

        for (ChessPiece.PieceType promotionPiece : promotionPieces) {
            //Add moving forward, if available
            ChessPosition forwardPosition = new ChessPosition(row + moveIncrement, column);
            if (AllMoves.SquareValid(forwardPosition) && board.getPiece(forwardPosition) == null) {
                moves.add(new ChessMove(currPosition, forwardPosition, promotionPiece));
            }
            //Add left attack, if available
            ChessPosition leftAttack = new ChessPosition(row + moveIncrement, column-1);
            if (AllMoves.SquareValid(leftAttack) &&
                    board.getPiece(leftAttack) != null &&
                    board.getTeamOfSquare(leftAttack) != team) {
                moves.add(new ChessMove(currPosition, leftAttack, promotionPiece));
            }
            //Add right attack, if available
            ChessPosition rightAttack = new ChessPosition(row + moveIncrement, column+1);
            if (AllMoves.SquareValid(rightAttack) &&
                    board.getPiece(rightAttack) != null &&
                    board.getTeamOfSquare(rightAttack) != team) {
                moves.add(new ChessMove(currPosition, rightAttack, promotionPiece));
            }

            //Add first move double, if available
            ChessPosition doubleForwardPosition = new ChessPosition(row + moveIncrement*2, column);
            if (AllMoves.SquareValid(doubleForwardPosition) &&
                    ((team == ChessGame.TeamColor.WHITE && row == 2) || (team == ChessGame.TeamColor.BLACK && row == 7)) &&
                    board.getPiece(doubleForwardPosition) == null &&
                    board.getPiece(forwardPosition) == null) {
                moves.add(new ChessMove(currPosition, doubleForwardPosition, promotionPiece));
            }

        }

        return moves;
    }
}
