package ui;

import exception.ResponseException;
import model.GameData;
import requests.*;
import results.*;

import java.util.*;

public class PostLoginREPL {

    private static final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade;
    private final Map<Integer, Integer> gameIndexMap = new HashMap<>();

    public PostLoginREPL(ServerFacade facade) {
        this.facade = facade;
    }

    public void start(LoginResult loginResult) {
        System.out.println("Now that you're logged in, type 'help' for a new list of commands.");

        while (true) {
            System.out.print("Post-Login> ");
            String command = scanner.nextLine().trim().toLowerCase();

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
        } catch (ResponseException e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private void handleCreateGame(LoginResult loginResult) {
        System.out.print("Enter a name for the new game: ");
        String gameName = scanner.nextLine();

        try {
            CreateGameRequest createGameRequest = new CreateGameRequest(loginResult.authToken(), gameName);
            facade.createGame(createGameRequest, loginResult);
            System.out.println("Game '" + gameName + "' created successfully!");
        } catch (ResponseException e) {
            System.out.println("Failed to create game: " + e.getMessage());
        }
    }

    private void handleListGames(LoginResult loginResult) {
        try {
            ListGamesResult listGamesResult = facade.listGames(new ListGamesRequest(loginResult.authToken()), loginResult);
            Collection<GameData> games = listGamesResult.games();

            System.out.println("Available games:");
            gameIndexMap.clear();

            int index = 1;
            for (GameData game : games) {
                String whitePlayer = game.whiteUsername() != null ? game.whiteUsername() : "open";
                String blackPlayer = game.blackUsername() != null ? game.blackUsername() : "open";
                System.out.println("Game Number:" + index + " | " + game.gameName() + " | White Player: " + whitePlayer + " | Black Player: " + blackPlayer);
                gameIndexMap.put(index, game.gameID());
                index++;
            }
        } catch (ResponseException e) {
            System.out.println("Failed to list games: " + e.getMessage());
        }
    }

    private void handleJoinGame(LoginResult loginResult) {
        if (gameIndexMap.isEmpty()) {
            System.out.println("No games available. Use 'list games' to see available games.");
            return;
        }

        System.out.print("Enter the number of the game you want to join: ");
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid game number.");
            return;
        }

        if (!gameIndexMap.containsKey(gameNumber)) {
            System.out.println("Invalid game number. Use the command 'list games' to see valid game numbers");
            return;
        }

        System.out.print("Enter color (WHITE/BLACK): ");
        String color = scanner.nextLine().toUpperCase();

        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            System.out.println("Invalid color. Please enter either 'WHITE' or 'BLACK'.");
            handleJoinGame(loginResult);
            return;
        }

        try {
            int gameId = gameIndexMap.get(gameNumber);
            JoinGameRequest joinRequest = new JoinGameRequest(loginResult.authToken(), color, gameId);
            facade.joinGame(joinRequest, loginResult);
            System.out.println("Joined game as " + color + "!");
//            displayBoard();
        } catch (ResponseException e) {
            System.out.println("Failed to join game: Color already taken");
            handleJoinGame(loginResult);
        }
    }

    private void handleObserveGame() {
        if (gameIndexMap.isEmpty()) {
            System.out.println("No games available.");
            return;
        }

        System.out.print("Enter the number of the game you want to observe: ");
        int gameNumber;
        try {
            gameNumber = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid game number.");
            return;
        }

        if (!gameIndexMap.containsKey(gameNumber)) {
            System.out.println("Invalid game number.");
            return;
        }

        int gameId = gameIndexMap.get(gameNumber);
        System.out.println("Observing game: " + gameId);
//        displayBoard();
    }
    }
