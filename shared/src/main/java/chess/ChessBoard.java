package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board;

    public ChessBoard() {

        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow() - 1;
        int col = position.getColumn() - 1;
        board[row][col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow() -1;
        int col = position.getColumn() - 1;
        return board[row][col];
    }

    public ChessGame.TeamColor getTeamOfSquare(ChessPosition position) {
        if (getPiece(position) != null) {
            return getPiece(position).getTeamColor();
        }
        else {return null;}
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];

       //Pawns :)
        for (int col = 1; col <=8; col++)
        {
            addPiece(new ChessPosition(2, col), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, col), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        //Knights :)

        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));

        //Bishops :)

        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));

        //Rooks :)
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        //Queens :)
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));

        //Kings :)
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));



    }

    public boolean toString(ChessGame.TeamColor perspective, Collection<ChessPosition> highlights) {
        if (board == null) {
            return false;
        }

        final String reset = "\u001B[0m";
        final String white_bg = "\u001B[47m"; // light square
        final String black_bg = "\u001B[40m"; // dark square
        final String red = "\u001B[31m";      // white pieces in red
        final String blue = "\u001B[34m";     // black pieces in blue

        StringBuilder sb = new StringBuilder();
        boolean whitePerspective = perspective == ChessGame.TeamColor.WHITE;

        // Column headers
        sb.append("   ");
        for (int col = 1; col <= 8; col++) {
            char colLabel = (char) ('a' + (whitePerspective ? col - 1 : 8 - col));
            sb.append(" ").append(colLabel).append(" ");
        }
        sb.append("\n");

        for (int r = 0; r < 8; r++) {
            int rowIndex = whitePerspective ? 8 - r : r + 1;
            sb.append(rowIndex).append(" "); // row label

            for (int c = 0; c < 8; c++) {
                int colIndex = whitePerspective ? c + 1 : 8 - c;
                ChessPosition pos = new ChessPosition(rowIndex, colIndex);
                ChessPiece piece = getPiece(pos);

                // Bottom-left is dark
                boolean isLightSquare = (rowIndex + colIndex) % 2 != 0;
                String bgColor = isLightSquare ? white_bg : black_bg;

                String pieceStr = " "; // empty square
                if (piece != null) {
                    pieceStr = (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? red : blue)
                            + piece;
                }

                sb.append(bgColor).append(" ").append(pieceStr).append(" ").append(reset);
            }

            sb.append(" ").append(rowIndex).append("\n"); // repeat row label
        }

        // Column headers again
        sb.append("   ");
        for (int col = 1; col <= 8; col++) {
            char colLabel = (char) ('a' + (whitePerspective ? col - 1 : 8 - col));
            sb.append(" ").append(colLabel).append(" ");
        }
        sb.append("\n");

        System.out.println(sb.toString());
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }


}
