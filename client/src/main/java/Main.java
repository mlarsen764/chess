import chess.*;
import ui.PreLoginREPL;
import ui.ServerFacade;

public class Main {
    public static void main(String[] args) {
//        System.out.println("♕ 240 Chess Client: ");
        ServerFacade facade = new ServerFacade("http://localhost:8080");

        PreLoginREPL preLoginREPL = new PreLoginREPL(facade);
        preLoginREPL.start();
        System.out.println("Exited");
    }
}