package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String client, Session session, ChessGame chessGame) {
        var connection = new Connection(client, session, chessGame);
        connections.put(client, connection);
    }

    public void remove(String client) {
        connections.remove(client);
    }

    public void broadcast(String excludePlayingClient, ServerMessage serverMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.chessPlayer.equals(excludePlayingClient)) {
                    c.send(serverMessage);
                }
            } else {
                removeList.add(c);
            }
        }

        //do I need this?? from pet shop
        //for (var c : removeList) {
        //connections.remove(c.chessPlayer);
        //}
    }

    public void sendToRootClient(String rootClient, ServerMessage serverMessage) throws IOException {
        Connection rootClientConnection = connections.get(rootClient);
        if (rootClientConnection.session.isOpen()) {
            rootClientConnection.send(serverMessage);
        }
    }
}
