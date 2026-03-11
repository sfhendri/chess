package chess;

import com.google.gson.Gson;

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

    public TeamColor whoseturn;
    public ChessBoard board;

    public ChessGame() {
        whoseturn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return whoseturn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.whoseturn = team;
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

        HashSet<ChessMove> possiblemoves = (HashSet<ChessMove>) board.getPiece(startPosition).pieceMoves(board, startPosition);
        HashSet<ChessMove> validmoves = HashSet.newHashSet(possiblemoves.size());

        for (ChessMove move : possiblemoves)
        {
            ChessPiece temporarypiece = board.getPiece(move.getEndPosition());
            board.addPiece(move.getStartPosition(), null); // Making the previous location of piece null
            board.addPiece(move.getEndPosition(), piece);
            if (!isInCheck(piece.getTeamColor()))
            {
                validmoves.add(move);
            }
            board.addPiece(move.getEndPosition(), temporarypiece);
            board.addPiece(move.getStartPosition(), piece);
        }
        return validmoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean teamsturn = getTeamTurn() == board.getTeamOfSquare(move.getStartPosition());
        Collection<ChessMove> availablemoves = validMoves(move.getStartPosition());
        if (availablemoves == null)
        {
          throw new InvalidMoveException("NO VALID MOVES");
        }
        boolean isvalidmove = availablemoves.contains(move);

        if (isvalidmove && teamsturn)
        {
            ChessPiece movingPiece = board.getPiece(move.getStartPosition());
            // Check to see if piece is going to be promoted
            if (move.getPromotionPiece() != null)
            {
                movingPiece = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
            }

            board.addPiece(move.getStartPosition(), null);
            board.addPiece(move.getEndPosition(), movingPiece);
            setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        }
        else
        {
            throw new InvalidMoveException(String.format("Valid move: %b  Your Turn: %b", isvalidmove, teamsturn));
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingposition = null;
        // Find the King
        for (int y = 1; y <= 8 && kingposition == null; y++)
        {
            for (int x = 1; x <=8 && kingposition == null; x++)
            {
                ChessPiece piece = board.getPiece(new ChessPosition(y,x));
                if (piece == null) {
                    continue;
                }
                if (piece.getTeamColor() == teamColor && piece.getPieceType().equals(ChessPiece.PieceType.KING))
                {
                    kingposition = new ChessPosition(y,x);
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
                    if (enemyMove.getEndPosition().equals(kingposition))
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
        if (!isInCheck(teamColor)) {
            return false;
        }

        // If the team has ANY valid move, it's not checkmate (Meaning a Piece can be taken)
        for (int y = 1; y <= 8; y++) {
            for (int x = 1; x <= 8; x++) {
                ChessPosition pos = new ChessPosition(y, x);
                ChessPiece piece = board.getPiece(pos);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor))
        {
            return false; //If in Check then Can't be in Stalemate
        }
        if (getTeamTurn() != teamColor)
        {
            return false; //Wrong Teams Turn
        }
        for (int y = 1; y <=8; y++){
            for (int x = 1; x <= 8; x++) {
                ChessPosition currentPosition = new ChessPosition(y,x);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                Collection<ChessMove> moves;

                if (currentPiece != null && teamColor.equals(currentPiece.getTeamColor()))
                {
                    moves = validMoves(currentPosition);
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

    public static ChessGame fromString(String serializedGame) {
        return new Gson().fromJson(serializedGame, ChessGame.class);
    }
    
    @Override
    public String toString() {
        return "ChessGame{" +
                "whoseturn=" + whoseturn +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return whoseturn == chessGame.whoseturn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(whoseturn, board);
    }
}
