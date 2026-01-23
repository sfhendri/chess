package chess;

import chess.AllMoves.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor Team;
    private final PieceType piece;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.Team = pieceColor;
        this.piece = type;
    }


    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return Team;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return piece;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (piece) {
            case KING -> KingMoves.getMoves(board, myPosition);
            case QUEEN -> QueenMoves.getMoves(board, myPosition);
            case BISHOP -> BishopMoves.getMoves(board, myPosition);
            case KNIGHT -> KnightMoves.getMoves(board, myPosition);
            case ROOK -> RookMoves.getMoves(board, myPosition);
            case PAWN -> null;
        };
    }

    @Override
    public String toString() {
        return switch (piece) {
            case KING -> Team == ChessGame.TeamColor.WHITE ? "K" : "k";
            case QUEEN -> Team == ChessGame.TeamColor.WHITE ? "Q" : "q";
            case BISHOP -> Team == ChessGame.TeamColor.WHITE ? "B" : "b";
            case KNIGHT -> Team == ChessGame.TeamColor.WHITE ? "N" : "n";
            case ROOK -> Team == ChessGame.TeamColor.WHITE ? "R" : "r";
            case PAWN -> Team == ChessGame.TeamColor.WHITE ? "P" : "p";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return Team == that.Team && piece == that.piece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Team, piece);
    }
}
