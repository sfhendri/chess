package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor Team;
    private final PieceType piece;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
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

        List<ChessMove> all_moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        switch (PieceType) {

            //Pawns First

            case PAWN -> {
                int direction = Your Mom;
            }
        }

    }
}
