package ui;

import exception.ResponseException;
import model.GameData;
import requests.*;
import results.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class PostLoginREPL {

    private static final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade;
    Collection<GameData> games;

    public PostLoginREPL(ServerFacade facade) {
        this.facade = facade;
        games = new ArrayList<>();
    }

    public void start() {
        System.out.println("Welcome to the Chess Client! Type 'help' for a list of commands.");

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "quit":
                    System.out.println("Goodbye!");
                    return;
                case "login":
                    handleLogin();
                    break;
                case "register":
                    handleRegister();
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
                    break;
            }
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("  help      - Displays this help message.");
        System.out.println("  quit      - Exits the program.");
        System.out.println("  login     - Log in to an existing account.");
        System.out.println("  register  - Create a new account and log in.");
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult loginResult = facade.login(loginRequest);
            System.out.println("Login successful! Transitioning to post-login UI.");
            // Transition to post-login UI
//            postLoginUI(loginResult);
        } catch (ResponseException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    private void handleRegister() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        try {
            RegistrationRequest registrationRequest = new RegistrationRequest(username, password, email);
            RegistrationResult registrationResult = facade.register(registrationRequest);
            System.out.println("Registration successful! Logging in and transitioning to post-login UI.");
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult loginResult = facade.login(loginRequest);
            // Transition to post-login UI
//            postLoginREPL.start();
        } catch (ResponseException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
}
