package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private
        ChessGame.TeamColor pieceColor;
        ChessPiece.PieceType type;
        ChessBoard board;
        ChessPosition myPosition;
        ChessPosition newPosition;
        Collection<ChessMove> validMoves = new ArrayList<>();

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChessPiece{" + "pieceColor = " + pieceColor + ", type = " + type + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) obj;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
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
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    public boolean onBoard() {
        return newPosition.getRow() < 8 && newPosition.getRow() >= 0 && newPosition.getColumn() < 8 && newPosition.getColumn() >= 0;
    }

    public boolean isValidMove() {
        return board.getPiece(newPosition) == null || board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor;
    }

    public void addMove() {
        validMoves.add(new ChessMove(myPosition, new ChessPosition(newPosition.getRow() + 1, newPosition.getColumn() + 1), null));
    }

    public void addMove(PieceType promotion) {
        validMoves.add(new ChessMove(myPosition, new ChessPosition(newPosition.getRow() + 1, newPosition.getColumn() + 1), promotion));
    }

    public void checkForValidMove(int incRow, int incCol) {
        //newPosition.update(incRow, incCol);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return new ArrayList<>();
    }
}
