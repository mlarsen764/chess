package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {1, -1},
                {-1, 1},
                {1, 1},
                {-1, -1}
        };

        ChessPiece currentPiece = board.getPiece(myPosition);

        for (int[] direction : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];

                ChessPosition newPosition = new ChessPosition(row, col);

                if (!newPosition.isValidPosition(row, col)) {
                    break;
                }

                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (currentPiece.isOpponent(pieceAtNewPosition)) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
        return moves;
    };
}
