package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import results.*;
import requests.*;

import java.util.*;

import static ui.EscapeSequences.*;

public class PostLoginREPL {
    private static final Scanner SCANNER = new Scanner(System.in);
    private final ServerFacade facade;
    private List<GameData> games = new ArrayList<>();
    StringBuilder gameString;

    public PostLoginREPL(ServerFacade facade) {
        this.facade = facade;
    }

    public void start(LoginResult loginResult) throws Exception {
        System.out.println("Now that you're logged in, type 'help' for a new list of commands.");

        while (true) {
            System.out.print("Post-Login> ");
            String command = SCANNER.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "create":
                    handleCreateGame(loginResult);
                    break;
                case "list":
                    handleListGames(loginResult);
                    break;
                case "join":
                    handleJoinGame(loginResult);
                    break;
                case "observe":
                    handleObserveGame();
                    break;
                case "logout":
                    handleLogout(loginResult);
                    return;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
                    break;
            }
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("  help      - Displays this help message.");
        System.out.println("  create    - Create a new chess game.");
        System.out.println("  list      - List all available games.");
        System.out.println("  join      - Join a game as a player.");
        System.out.println("  observe   - Observe a game as a spectator.");
        System.out.println("  logout    - Log out of the current session.");
    }

    private void handleLogout(LoginResult loginResult) {
        try {
            facade.logout(loginResult);
            System.out.println("You have been logged out.");
        } catch (Exception e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private void handleCreateGame(LoginResult loginResult) {
        System.out.print("Enter a name for the new game: ");
        String gameName = SCANNER.nextLine();

        try {
            GameData newGame = facade.createGame(new GameData(0, null, null, gameName, new ChessGame()), loginResult);
            System.out.println("Game created: " + newGame.game());
            games.add(newGame);
            System.out.println("Game '" + gameName + "' created successfully!");
        } catch (Exception e) {
            System.out.println("Failed to create game: " + e.getMessage());
        }
    }

    private void handleListGames(LoginResult loginResult) {
        try {
            ListGamesResult listGamesResult = facade.listGames(new ListGamesRequest(loginResult.authToken()), loginResult);
            games = listGamesResult.games();

            if (games.isEmpty()) {
                System.out.println("No games found. Use 'create' to make a new game!");
                return;
            }

            System.out.println("Available games:");
            for (int i = 0; i < games.size(); i++) {
                GameData game = games.get(i);
                String whitePlayer = game.whiteUsername() != null ? game.whiteUsername() : "open";
                String blackPlayer = game.blackUsername() != null ? game.blackUsername() : "open";
                System.out.println("Game Number: " + (i + 1) + " | " + game.gameName() +
                        " | White Player: " + whitePlayer + " | Black Player: " + blackPlayer);
            }
        } catch (Exception e) {
            System.out.println("Failed to list games: " + e.getMessage());
        }
    }

    private void handleJoinGame(LoginResult loginResult) {
        if (games.isEmpty()) {
            System.out.println("No games available yet, use list to list games first!");
            return;
        }

        System.out.print("Enter the number of the game you want to join: ");
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(SCANNER.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid game number.");
            return;
        }

        if (gameNumber < 0 || gameNumber >= games.size()) {
            System.out.println("Invalid game number. Use 'list' to see valid game numbers.");
            return;
        }

        System.out.print("Enter color (WHITE/BLACK): ");
        String color = SCANNER.nextLine().toUpperCase();

        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            System.out.println("Invalid color. Please enter either 'WHITE' or 'BLACK'.");
            handleJoinGame(loginResult);
            return;
        }

        try {
            int gameId = games.get(gameNumber).gameID();
            JoinGameRequest joinRequest = new JoinGameRequest(loginResult.authToken(), color, gameId);
            facade.joinGame(joinRequest, loginResult);
            System.out.println("Joined game as " + color + "!");
            printBoard();
        } catch (Exception e) {
            System.out.println("Failed to join game: Color already taken");
        }
    }

    private void handleObserveGame() throws Exception {
        if (games.isEmpty()) {
            System.out.println("No games available.");
            return;
        }

        System.out.print("Enter the number of the game you want to observe: ");
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(SCANNER.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Use 'list' to see valid game numbers.");
            return;
        }

        if (gameNumber < 0 || gameNumber >= games.size()) {
            System.out.println("Invalid game number.");
            return;
        }

        System.out.println("Observing game: " + games.get(gameNumber).gameID());
        printBoard();
    }

    private void printBoard() throws Exception {
        gameString = new StringBuilder();
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();
        buildBoard(board);

        gameString.append(SET_BG_COLOR_BLACK
                + "                                   "
                + RESET_BG_COLOR
                + "\n");

        buildBackwardsBoard(board);
    }

    private void buildBoard(ChessBoard board) {
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

    private void buildBackwardsBoard(ChessBoard board) {
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
        System.out.println(gameString);
    }

    private void addPiece(ChessBoard board, int i, int j) {
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
