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
        newPosition.updatePosition(incRow, incCol);
        while (onBoard()) {
            if (isValidMove()) {
                addMove();
            }
            if (board.getPiece(newPosition) != null) {
                break;
            }
            newPosition.updatePosition(incRow, incCol);
        }
        newPosition.resetPosition(myPosition);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.newPosition = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);

        //Rook Moves
        if (board.getPiece(myPosition).type == PieceType.ROOK) {
            int incRow = 1;
            int incCol = 0;
            for (int i = 0; i < 4; i++) {
                checkForValidMove(incRow, incCol);
                switch (i) {
                    case 0:
                        incRow = 0;
                        incCol = 1;
                        break;
                    case 1:
                        incRow = -1;
                        incCol = 0;
                        break;
                    case 2:
                        incRow = 0;
                        incCol = -1;
                        break;
                }
            }
        }
        //Knight Moves
        else if (board.getPiece(myPosition).type == PieceType.KNIGHT) {
            int incRow = 2;
            int incCol = 1;
            for (int i = 0; i < 8; i++) {
                newPosition.updatePosition(incRow, incCol);
                if (onBoard() && isValidMove()) {
                    addMove();
                }
                newPosition.resetPosition(myPosition);
                int temp = incRow;
                incRow = incCol;
                incCol = temp;
                if (i % 4 == 0) {
                    incRow *= -1;
                }
                else if (i % 2 == 0) {
                    incCol *= -1;
                }
            }
        }
        //Bishop Moves
        else if (board.getPiece(myPosition).type == PieceType.BISHOP) {
            int incRow = 1;
            int incCol = 1;
            for (int i = 0; i < 4; i++) {
                checkForValidMove(incRow, incCol);
                if (i % 2 == 0) {
                    incRow *= -1;
                } else {
                    incCol *= -1;
                }
            }
        }
        //Queen Moves
        else if (board.getPiece(myPosition).type == PieceType.QUEEN) {
            int incRow = 1;
            int incCol = 0;
            for (int i = 0; i < 8; i++) {
                switch (i) {
                    case 0:
                        incRow = 0;
                        incCol = 1;
                        break;
                    case 1:
                        incRow = -1;
                        incCol = 0;
                        break;
                    case 2:
                        incRow = 0;
                        incCol = -1;
                    case 3:
                        incRow = 1;
                        incCol = 1;
                    default:
                        if (i % 2 == 0) {
                            incRow *= -1;
                        } else {
                            incCol *= -1;
                        }
                }
            }
        }
        //King Moves
        else if (board.getPiece(myPosition).type == PieceType.KING) {
            int incRow = 1;
            int incCol = 0;
            for (int i = 0; i < 8; i++) {
                newPosition.updatePosition(incRow, incCol);
                if (onBoard()) {
                    if(isValidMove()) {
                        addMove();
                    }
                }
                newPosition.resetPosition(myPosition);
                switch (i) {
                    case 0:
                        incRow = 0;
                        incCol = 1;
                        break;
                    case 1:
                        incRow = -1;
                        incCol = 0;
                        break;
                    case 2:
                        incRow = 0;
                        incCol = -1;
                        break;
                    case 3:
                        incRow = 1;
                        incCol = 1;
                        break;
                    default:
                        if (i % 2 == 0) {
                            incRow *= -1;
                        } else {
                            incCol *= -1;
                        }
                }
            }
        }
        //Pawn Moves
        else if (board.getPiece(myPosition).type == PieceType.PAWN) {
            if (board.getPiece(myPosition).pieceColor == ChessGame.TeamColor.WHITE) {
                int incRow = 1;
                int incCol = -1;
                for (int i = 0; i < 3; i++) {
                    newPosition.updatePosition(incRow, incCol);
                    if (i % 2 == 0 && board.getPiece(newPosition) != null && onBoard() && board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor) {
                        if (myPosition.getRow() == 6) {
                            addMove(PieceType.ROOK);
                            addMove(PieceType.KNIGHT);
                            addMove(PieceType.BISHOP);
                            addMove(PieceType.QUEEN);
                        } else {
                            addMove();
                        }
                    }
                    else if (i % 2 == 1 && onBoard() && board.getPiece(newPosition) == null) {
                        if (myPosition.getRow() == 6) {
                            addMove(PieceType.ROOK);
                            addMove(PieceType.KNIGHT);
                            addMove(PieceType.BISHOP);
                            addMove(PieceType.QUEEN);
                        } else {
                            addMove();
                        }
                        if(myPosition.getRow() == 1) {
                            if(onBoard() && board.getPiece(newPosition) == null) {
                                addMove();
                            }
                        }
                    }
                    newPosition.resetPosition(myPosition);
                    incCol++;
                }
            } else {
                int incRow = -1;
                int incCol = -1;
                for (int i = 0; i < 3; i++) {
                    newPosition.updatePosition(incRow, incCol);
                    if (i % 2 == 0 && board.getPiece(newPosition) != null && onBoard() && board.getPiece(newPosition).pieceColor != board.getPiece(myPosition).pieceColor) {
                        if (myPosition.getRow() == 1) {
                            addMove(PieceType.ROOK);
                            addMove(PieceType.KNIGHT);
                            addMove(PieceType.BISHOP);
                            addMove(PieceType.QUEEN);
                        } else {
                            addMove();
                        }
                    } else if (i % 2 == 1 && board.getPiece(newPosition) == null && onBoard()) {
                        if (myPosition.getRow() == 1) {
                            addMove(PieceType.ROOK);
                            addMove(PieceType.KNIGHT);
                            addMove(PieceType.BISHOP);
                            addMove(PieceType.QUEEN);
                        } else {
                            addMove();
                        }
                        if (myPosition.getRow() == 1) {
                            if (onBoard() && board.getPiece(newPosition) == null) {
                                addMove();
                            }
                        }
                    }
                    newPosition.resetPosition(myPosition);
                    incCol++;
                }
            }
        }
        return validMoves;
    }
}
