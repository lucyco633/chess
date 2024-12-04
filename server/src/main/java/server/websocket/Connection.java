package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

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

    public void send(ServerMessage serverMessage) throws IOException {
        session.getRemote().sendString(new Gson().toJson(serverMessage));
    }
}
