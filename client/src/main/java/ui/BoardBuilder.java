package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import static ui.EscapeSequences.*;

public class BoardBuilder {

    static StringBuilder gameString;

    private static String gameToString(GameData gameData) throws Exception {
        gameString = new StringBuilder();
        ChessGame game = gameData.game();
        ChessBoard board = game.getBoard();

        buildBoard(board);

        gameString.append(SET_BG_COLOR_BLACK
                + "                                   "
                + RESET_BG_COLOR
                + "\n");

        buildBackwardsBoard(board);

        return gameString.toString();
    }

    private static void buildBoard(ChessBoard board) {
        for (int i = 0; i < 10; i++) {
            if (i == 0 || i == 9) {
                gameString.append(SET_BG_COLOR_DARK_GREY
                        +  SET_TEXT_COLOR_WHITE
                        + "    h   g   f  e   d   c  b   a    "
                        + RESET_BG_COLOR + "\n");
            } else if (i % 2 != 0) {
                for (int j = 0; j < 10; j++) {
                    if (j == 0 || j == 9) {
                        gameString.append(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + " ").
                                append(i).append(" ").append(RESET_BG_COLOR);
                    } else if (j % 2 != 0) {
                        gameString.append(SET_BG_COLOR_LIGHT_GREY);
                        addPiece(board, i, j);
                    } else {
                        gameString.append(SET_BG_COLOR_BLACK);
                        addPiece(board, i, j);
                    }
                }
                gameString.append("\n");
            } else {
                for (int k = 0; k < 10; k++) {
                    if (k == 0 || k == 9) {
                        gameString.append(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + " ").
                                append(i).append(" ").append(RESET_BG_COLOR);
                    } else if (k % 2 != 0) {
                        gameString.append(SET_BG_COLOR_BLACK);
                        addPiece(board, i, k);
                    } else {
                        gameString.append(SET_BG_COLOR_LIGHT_GREY);
                        addPiece(board, i, k);
                    }
                }
                gameString.append("\n");
            }
        }
    }

    private static void buildBackwardsBoard(ChessBoard board) {
        for (int i = 9; i >= 0; i--) { // Loop backwards for rows
            if (i == 9 || i == 0) {
                gameString.append(SET_BG_COLOR_DARK_GREY
                        + SET_TEXT_COLOR_WHITE
                        + "    a   b   c  d   e   f  g   h    " // Columns are in reverse order
                        + RESET_BG_COLOR + "\n");
            } else if (i % 2 != 0) {
                for (int j = 9; j >= 0; j--) { // Loop backwards for columns
                    if (j == 9 || j == 0) {
                        gameString.append(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + " ")
                                .append(i).append(" ").append(RESET_BG_COLOR);
                    } else if (j % 2 != 0) {
                        gameString.append(SET_BG_COLOR_LIGHT_GREY);
                        addPiece(board, i, j);
                    } else {
                        gameString.append(SET_BG_COLOR_BLACK);
                        addPiece(board, i, j);
                    }
                }
                gameString.append("\n");
            } else {
                for (int k = 9; k >= 0; k--) { // Loop backwards for columns
                    if (k == 9 || k == 0) {
                        gameString.append(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + " ")
                                .append(i).append(" ").append(RESET_BG_COLOR);
                    } else if (k % 2 != 0) {
                        gameString.append(SET_BG_COLOR_BLACK);
                        addPiece(board, i, k);
                    } else {
                        gameString.append(SET_BG_COLOR_LIGHT_GREY);
                        addPiece(board, i, k);
                    }
                }
                gameString.append("\n");
            }
        }
    }

    private static void addPiece(ChessBoard board, int i, int j) {
        if (board.getPiece(new ChessPosition(i, j)) == null) {
            gameString.append(EMPTY);
        } else {
            ChessPiece piece = board.getPiece(new ChessPosition(i, j));
            switch(piece.type) {
                case KING -> {
                    switch (piece.pieceColor) {
                        case WHITE -> gameString.append(WHITE_KING);
                        case BLACK -> gameString.append(BLACK_KING);
                    }
                }
                case QUEEN -> {
                    switch (piece.pieceColor) {
                        case WHITE -> gameString.append(WHITE_QUEEN);
                        case BLACK -> gameString.append(BLACK_QUEEN);
                    }
                }
                case ROOK -> {
                    switch (piece.pieceColor) {
                        case WHITE -> gameString.append(WHITE_ROOK);
                        case BLACK -> gameString.append(BLACK_ROOK);
                    }
                }
                case BISHOP -> {
                    switch (piece.pieceColor) {
                        case WHITE -> gameString.append(WHITE_BISHOP);
                        case BLACK -> gameString.append(BLACK_BISHOP);
                    }
                }
                case KNIGHT -> {
                    switch (piece.pieceColor) {
                        case WHITE -> gameString.append(WHITE_KNIGHT);
                        case BLACK -> gameString.append(BLACK_KNIGHT);
                    }
                }
                case PAWN -> {
                    switch (piece.pieceColor) {
                        case WHITE -> gameString.append(WHITE_PAWN);
                        case BLACK -> gameString.append(BLACK_PAWN);
                    }
                }
            }
        }
    }

}