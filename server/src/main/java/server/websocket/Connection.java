package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String chessPlayer;
    public Session session;
    public ChessGame chessGame;

    public Connection(String chessPlayer, Session session, ChessGame chessGame) {
        this.chessPlayer = chessPlayer;
        this.session = session;
        this.chessGame = chessGame;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
