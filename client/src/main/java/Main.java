import chess.*;
import ui.PreLoginREPL;
import ui.ServerFacade;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ServerFacade facade = new ServerFacade("http://localhost:5000");

        PreLoginREPL preLoginREPL = new PreLoginREPL(facade);
        preLoginREPL.start();
        System.out.println("Exited");
    }
}