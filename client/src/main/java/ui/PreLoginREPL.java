package ui;

import exception.ResponseException;
import requests.*;
import results.*;

import java.util.Scanner;

public class PreLoginREPL {

    private static final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade;
    PostLoginREPL postLoginREPL;

    public PreLoginREPL(ServerFacade facade) {
        this.facade = facade;
        PostLoginREPL postLoginREPL = new PostLoginREPL(facade);
    }

    public void start() {
        System.out.println("Welcome to Chess! Type 'help' for a list of commands.");

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
            System.out.println("Login successful!");
            postLoginREPL.start(loginResult);
        } catch (ResponseException e) {
            System.out.println("Login failed: Incorrect username or password");
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
            System.out.println("Registration successful! You are now logged in.");
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult loginResult = facade.login(loginRequest);
            postLoginREPL.start(loginResult);
        } catch (ResponseException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
}
