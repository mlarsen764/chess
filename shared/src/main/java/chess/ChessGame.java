package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return getTeamTurn() == chessGame.getTeamTurn() && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamTurn(), getBoard());
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private ChessPosition findKing(TeamColor teamColor, ChessBoard board) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor && currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    return currentPosition;
                }
            }
        }
        return null;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPiece = board.getPiece(startPosition);
        // return moves that don't leave your king in check
        Collection<ChessMove> pieceMoves = currentPiece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for(ChessMove move : pieceMoves) {
            ChessBoard boardCopy = board.clone();
            boardCopy.addPiece(move.getStartPosition(), null);
            boardCopy.addPiece(move.getEndPosition(), currentPiece);
            if(!isInCheck(currentPiece.getTeamColor())) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece currentPiece = board.getPiece(startPosition);

        Collection<ChessMove> validMoves = validMoves(startPosition);
        boolean isValidMove = validMoves.contains(move);

        if (isValidMove && getTeamTurn() == currentPiece.getTeamColor()) {
            int oldRow = startPosition.getRow();
            int oldCol = startPosition.getColumn();
            int newRow = endPosition.getRow();
            int newCol = endPosition.getColumn();

            ChessPosition oldPosition = new ChessPosition(oldRow, oldCol);
            ChessPosition newPosition = new ChessPosition(newRow, newCol);

            board.addPiece(newPosition, currentPiece);
            board.addPiece(oldPosition, null);
            setTeamTurn(this.teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        } else {
            throw new InvalidMoveException("Invalid Move.");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // iterate over opposing player's moves to see if any of the end positions include the king's position.
        ChessPosition kingPosition = findKing(teamColor, board);

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col ++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece == null || currentPiece.getTeamColor() == teamColor) {
                    continue;
                }
                for (ChessMove opponentMove : currentPiece.pieceMoves(board, currentPosition)) {
                    if (opponentMove.getEndPosition().equals(kingPosition)) {
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
        // if you're not in check, you're not in checkmate. Apply moves and call isincheck for each one.
        // Iterate the moves, copy the board (getBoard().clone), make the move, call isincheck. If any get out of check, not in checkmate
        return isInCheck(teamColor) && isInStalemate(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                Collection<ChessMove> moves;
                if (currentPiece != null && teamColor == currentPiece.getTeamColor()) {
                    moves = validMoves(currentPosition);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
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
}
