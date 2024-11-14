package ui;

public class BoardBuilder {

    private static final int BOARD_SIZE = 8;
    private final String[][] board;

    public BoardBuilder() {
        board = new String[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    board[row][col] = EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR;
                } else {
                    board[row][col] = EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR;
                }
            }
        }
    }

    public void placePiece(int row, int col, String piece, boolean isWhite) {
        String color = isWhite ? EscapeSequences.SET_TEXT_COLOR_WHITE : EscapeSequences.SET_TEXT_COLOR_BLACK;
        String bgColor = (row + col) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;

        board[row][col] = color + bgColor + piece + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;
    }

    // Populate initial pieces for a new chess game
    public void setupNewGame() {
        // Place pawns
        for (int col = 0; col < BOARD_SIZE; col++) {
            placePiece(1, col, EscapeSequences.BLACK_PAWN, false);
            placePiece(6, col, EscapeSequences.WHITE_PAWN, true);
        }

        // Place other pieces
        placePiece(0, 0, EscapeSequences.BLACK_ROOK, false);
        placePiece(0, 7, EscapeSequences.BLACK_ROOK, false);
        placePiece(7, 0, EscapeSequences.WHITE_ROOK, true);
        placePiece(7, 7, EscapeSequences.WHITE_ROOK, true);

        placePiece(0, 1, EscapeSequences.BLACK_KNIGHT, false);
        placePiece(0, 6, EscapeSequences.BLACK_KNIGHT, false);
        placePiece(7, 1, EscapeSequences.WHITE_KNIGHT, true);
        placePiece(7, 6, EscapeSequences.WHITE_KNIGHT, true);

        placePiece(0, 2, EscapeSequences.BLACK_BISHOP, false);
        placePiece(0, 5, EscapeSequences.BLACK_BISHOP, false);
        placePiece(7, 2, EscapeSequences.WHITE_BISHOP, true);
        placePiece(7, 5, EscapeSequences.WHITE_BISHOP, true);

        placePiece(0, 3, EscapeSequences.BLACK_QUEEN, false);
        placePiece(0, 4, EscapeSequences.BLACK_KING, false);
        placePiece(7, 3, EscapeSequences.WHITE_QUEEN, true);
        placePiece(7, 4, EscapeSequences.WHITE_KING, true);
    }

    // Render the chessboard in the default orientation with white at the bottom
    public void renderBoardWhite() {
        System.out.println(EscapeSequences.ERASE_SCREEN);  // Clear the screen
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                System.out.print(board[row][col]);
            }
            System.out.println(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
        }
    }


    public void renderBoardBlack() {
        System.out.println(EscapeSequences.ERASE_SCREEN);  // Clear the screen
        for (int row = BOARD_SIZE - 1; row >= 0; row--) {
            for (int col = BOARD_SIZE - 1; col >= 0; col--) {
                System.out.print(board[row][col]);
            }
            System.out.println(EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR);
        }
    }
}
