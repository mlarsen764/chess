package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece currentPiece = board.getPiece(myPosition);
        ChessGame.TeamColor teamColor = currentPiece.getTeamColor();
        int direction = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int promotionRow = (teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1;

        ChessPosition oneForward = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (oneForward.isValidPosition()) {
            if (board.getPiece(oneForward) == null) {
                if (oneForward.getRow() == promotionRow) {
                    moves.add(new ChessMove(myPosition, oneForward, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, oneForward, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, oneForward, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, oneForward, ChessPiece.PieceType.ROOK));
                } else {
                    moves.add(new ChessMove(myPosition, oneForward, null));
                }

                int startRow = (teamColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
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
                    if(capturePosition.getRow() == promotionRow) {
                        moves.add(new ChessMove(myPosition, oneForward, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, oneForward, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, oneForward, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, oneForward, ChessPiece.PieceType.ROOK));
                    } else {
                        moves.add(new ChessMove(myPosition, capturePosition, null));
                    }
                }
            }
        }
        return moves;
    }
}
