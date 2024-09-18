package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece currentPiece = board.getPiece(myPosition);
        int direction = (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;

        ChessPosition oneForward = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (oneForward.isValidPosition()) {
            if (board.getPiece(oneForward) == null) {
                moves.add(new ChessMove(myPosition, oneForward, null));

                int startRow = (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
                if (myPosition.getRow() == startRow) {
                    ChessPosition twoForward = new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn());
                    if (twoForward.isValidPosition() && board.getPiece(twoForward) == null) {
                        moves.add(new ChessMove(myPosition, twoForward, null));
                    }
                }
            }
        }

        int[] captureDirections = {-1, 1};
        for (int colDirection : captureDirections) {
            ChessPosition capturePosition = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + colDirection);
            if (capturePosition.isValidPosition()) {
                ChessPiece pieceAtCapturePosition = board.getPiece(capturePosition);
                if (pieceAtCapturePosition != null && pieceAtCapturePosition.isOpponent(pieceAtCapturePosition)) {
                    moves.add(new ChessMove(myPosition, capturePosition, null));
                }
            }
        }
        return moves;
    }
}
