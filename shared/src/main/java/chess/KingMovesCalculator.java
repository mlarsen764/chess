package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] kingDirections = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1},
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
        };

        ChessPiece currentPiece = board.getPiece(myPosition);

        for (int[] direction : kingDirections) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            row += direction[0];
            col += direction[1];

            ChessPosition newKingPosition = new ChessPosition(row, col);

            if (!newKingPosition.isValidPosition()) {
                continue;
            }

            ChessPiece pieceAtNewPosition = board.getPiece(newKingPosition);

            if (pieceAtNewPosition == null) {
                moves.add(new ChessMove(myPosition, newKingPosition, null));
            } else {
                if (currentPiece.isOpponent(pieceAtNewPosition)) {
                    moves.add(new ChessMove(myPosition, newKingPosition, null));
                }
            }
        }
        return moves;
    };
}
