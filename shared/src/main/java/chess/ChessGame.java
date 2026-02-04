package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor WhoseTurn;
    private ChessBoard board;

    public ChessGame() {
        WhoseTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return WhoseTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.WhoseTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            return null;
        }

        HashSet<ChessMove> Possible_Moves = (HashSet<ChessMove>) board.getPiece(startPosition).pieceMoves(board, startPosition);
        HashSet<ChessMove> Valid_Moves = HashSet.newHashSet(Possible_Moves.size());

        for (ChessMove move : Possible_Moves)
        {
            ChessPiece Temporary_Piece = board.getPiece(move.getEndPosition());
            board.addPiece(move.getStartPosition(), null); // Making the previous location of piece null
            board.addPiece(move.getEndPosition(), piece);
            if (!isInCheck(piece.getTeamColor()))
            {
                Valid_Moves.add(move);
            }
            board.addPiece(move.getEndPosition(), Temporary_Piece);
            board.addPiece(move.getStartPosition(), piece);
        }
        return Valid_Moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean teams_turn = getTeamTurn() == board.getTeamOfSquare(move.getStartPosition());
        Collection<ChessMove> available_moves = validMoves(move.getStartPosition());
        if (available_moves == null)
        {
          throw new InvalidMoveException("NO VALID MOVES");
        }
        boolean is_valid_move = available_moves.contains(move);

        if (is_valid_move && teams_turn)
        {
            ChessPiece moving_piece = board.getPiece(move.getStartPosition());
            // Check to see if piece is going to be promoted
            if (move.getPromotionPiece() != null)
            {
                moving_piece = new ChessPiece(moving_piece.getTeamColor(), move.getPromotionPiece());
            }

            board.addPiece(move.getStartPosition(), null);
            board.addPiece(move.getEndPosition(), moving_piece);
            setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        }
        else
        {
            throw new InvalidMoveException(String.format("Valid move: %b  Your Turn: %b", is_valid_move, teams_turn));
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition King_Position = null;
        // Find the King
        for (int y = 1; y <= 8 && King_Position == null; y++)
        {
            for (int x = 1; x <=8 && King_Position == null; x++)
            {
                ChessPiece piece = board.getPiece(new ChessPosition(y,x));
                if (piece == null) {
                    continue;
                }
                if (piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING)
                {
                    King_Position = new ChessPosition(y,x);
                }
            }
        }

        // Must Check to see if an opposing piece can attack the king
        for (int y = 1; y <= 8; y++)
        {
            for (int x = 1; x <= 8; x++)
            {
                ChessPiece piece = board.getPiece(new ChessPosition(y,x));
                if (piece == null || piece.getTeamColor() == teamColor) {
                    continue;
                }
                for (ChessMove enemyMove : piece.pieceMoves(board, new ChessPosition(y,x)))
                {
                    if (enemyMove.getEndPosition().equals(King_Position))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInStalemate(teamColor) && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for (int y = 1; y <=8; y++){
            for (int x = 1; x <= 8; x++) {
                ChessPosition Current_Position = new ChessPosition(y,x);
                ChessPiece Current_Piece = board.getPiece(Current_Position);
                Collection<ChessMove> moves;

                if (Current_Piece != null && teamColor.equals(Current_Piece.getTeamColor()))
                {
                    moves = validMoves(Current_Position);
                    if (moves != null && !moves.isEmpty())
                    {
                        return false; // There is at least one move
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "WhoseTurn=" + WhoseTurn +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return WhoseTurn == chessGame.WhoseTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(WhoseTurn, board);
    }
}
