package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    //put into another map that has game id as key
    public final Map<Integer, ConcurrentHashMap<String, Connection>> connections = new HashMap<>();

    public void add(String client, Session session, ChessGame chessGame, int gameID) {
        var connection = new Connection(client, session, chessGame);
        //connections.put(client, connection);
        ConcurrentHashMap<String, Connection> newConnection = new ConcurrentHashMap<>();
        newConnection.put(client, connection);
        if (connections.get(gameID) == null) {
            connections.put(gameID, newConnection);
        } else {
            connections.get(gameID).put(client, connection);
        }
    }

    public void remove(String client, int gameId) {
        connections.get(gameId).remove(client);
    }

    public void broadcast(String excludePlayingClient, ServerMessage serverMessage, int gameId) throws IOException {
        var removeList = new ArrayList<Connection>();
        var currentClient = connections.get(gameId);
        for (var c : currentClient.values()) {
            if (c.session.isOpen()) {
                if (!c.chessPlayer.equals(excludePlayingClient)) {
                    c.send(serverMessage);
                }
            } else {
                removeList.add(c);
            }
        }
    }

    public void sendToClient(String rootClient, ServerMessage serverMessage, int gameId) throws IOException {
        Connection rootClientConnection = connections.get(gameId).get(rootClient);
        if (rootClientConnection.session.isOpen()) {
            rootClientConnection.send(serverMessage);
        }
    }
}
