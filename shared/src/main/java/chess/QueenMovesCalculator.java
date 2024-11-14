package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator extends PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
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

        for (int[] direction : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += direction[0];
                col += direction[1];

                ChessPosition newQueenPosition = new ChessPosition(row, col);

                if (!newQueenPosition.isValidPosition()) {
                    break;
                }

                ChessPiece pieceAtNewPosition = board.getPiece(newQueenPosition);

                if (pieceAtNewPosition == null) {
                    moves.add(new ChessMove(myPosition, newQueenPosition, null));
                } else {
                    if (currentPiece.isOpponent(pieceAtNewPosition)) {
                        moves.add(new ChessMove(myPosition, newQueenPosition, null));
                    }
                    break;
                }
            }
        }
        return moves;
    };
}
