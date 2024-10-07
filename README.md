# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Sequence Diagram Link:

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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
##
