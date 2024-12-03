package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.mysql.cj.protocol.x.Notice;
import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import dataaccess.SqlUserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.ResultExceptions;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@WebSocket
public class WebSocketServer {

    //create complex data structure that will hold onto game, clients, and session
    //add functionality to receive user commands and send server messages
    private final ConnectionManager connections = new ConnectionManager();
    public static SqlGameDAO sqlGameDAO;
    public static SqlAuthDAO sqlAuthDAO;
    public static SqlUserDAO sqlUserDAO;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {

        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        MakeMoveCommand makeMoveCommand;

        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
            makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
        }
    }

    public void connect(UserGameCommand userGameCommand, Session session) throws SQLException,
            ResultExceptions, DataAccessException, IOException {
        connections.add(sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username(), session,
                sqlGameDAO.getGame(userGameCommand.getGameID()).game());
        String message = sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username() + "joined game as observer";
        if (Objects.equals(sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username(),
                sqlGameDAO.getGame(userGameCommand.getGameID()).blackUsername())) {
            message = sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username() + "joined game as team black";
        } else if (Objects.equals(sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username(),
                sqlGameDAO.getGame(userGameCommand.getGameID()).whiteUsername())) {
            message = sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username() + "joined game as team white";
        }
        ServerMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                new Gson().toJson(sqlGameDAO.getGame(userGameCommand.getGameID()).game()));
        connections.broadcast(null, loadGameMessage);
        ServerMessage serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username(), serverMessage);
    }

    public void makeMove(MakeMoveCommand makeMoveCommand, Session session) throws ResultExceptions,
            InvalidMoveException, IOException, SQLException, DataAccessException {
        GameData gameData = sqlGameDAO.getGame(makeMoveCommand.getGameID());
        ChessGame chessGame = gameData.game();
        chessGame.makeMove(makeMoveCommand.getChessMove());
        ServerMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                new Gson().toJson(sqlGameDAO.getGame(makeMoveCommand.getGameID()).game()));
        connections.broadcast(sqlAuthDAO.getAuth(makeMoveCommand.getAuthToken()).username(), loadGameMessage);
    }

}
