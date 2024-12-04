package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
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
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

@WebSocket
public class WebSocketServer {

    //create complex data structure that will hold onto game, clients, and session
    //add functionality to receive user commands and send server messages
    private final ConnectionManager connections = new ConnectionManager();
    public static SqlGameDAO sqlGameDAO;

    static {
        try {
            sqlGameDAO = new SqlGameDAO();
        } catch (ResultExceptions e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static SqlAuthDAO sqlAuthDAO;

    static {
        try {
            sqlAuthDAO = new SqlAuthDAO();
        } catch (ResultExceptions e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws SQLException, IOException, ResultExceptions,
            DataAccessException, InvalidMoveException {

        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        MakeMoveCommand makeMoveCommand = null;
        if (userGameCommand.getCommandType().equals(UserGameCommand.CommandType.MAKE_MOVE)) {
            makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
        }

        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(userGameCommand, session);
            case MAKE_MOVE -> makeMove(makeMoveCommand, session);
            case RESIGN -> resign(userGameCommand, session);
            case LEAVE -> leave(userGameCommand, session);
        }
    }

    public void connect(UserGameCommand userGameCommand, Session session) throws IOException {
        try {
            connections.add(sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username(), session,
                    sqlGameDAO.getGame(userGameCommand.getGameID()).game());
            String message = sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username() + " joined game as observer";
            if (Objects.equals(sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username(),
                    sqlGameDAO.getGame(userGameCommand.getGameID()).blackUsername())) {
                message = sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username() + " joined game as team black";
            } else if (Objects.equals(sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username(),
                    sqlGameDAO.getGame(userGameCommand.getGameID()).whiteUsername())) {
                message = sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username() + " joined game as team white";
            }
            String rootClientUsername = sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username();
            ServerMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    new Gson().toJson(sqlGameDAO.getGame(userGameCommand.getGameID()).game()));
            connections.sendToRootClient(rootClientUsername, loadGameMessage);
            ServerMessage serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(rootClientUsername, serverMessage);
        } catch (SQLException | ResultExceptions | DataAccessException | IOException e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: " + e.getMessage());
            connections.broadcast(null, errorMessage);
        }
    }

    public void makeMove(MakeMoveCommand makeMoveCommand, Session session) throws IOException {
        try {
            int gameId = makeMoveCommand.getGameID();
            String authToken = makeMoveCommand.getAuthToken();
            ChessMove chessMove = makeMoveCommand.getChessMove();
            if (sqlGameDAO.getGame(gameId) == null) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid game ID");
                connections.broadcast(null, errorMessage);
            } else if (sqlAuthDAO.getAuth(authToken) == null | connections.connections.contains(
                    new Connection(sqlAuthDAO.getAuth(authToken).username(), session,
                            sqlGameDAO.getGame(makeMoveCommand.getGameID()).game()))) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid user token");
                connections.broadcast(null, errorMessage);
            } else if (!sqlGameDAO.getGame(gameId).game().validMoves(chessMove.getStartPosition()).contains(chessMove)) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid move");
                connections.broadcast(null, errorMessage);
            } else {
                GameData gameData = sqlGameDAO.getGame(makeMoveCommand.getGameID());
                ChessGame chessGame = gameData.game();
                chessGame.makeMove(makeMoveCommand.getChessMove());
                ServerMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                        new Gson().toJson(sqlGameDAO.getGame(makeMoveCommand.getGameID()).game()));
                connections.broadcast(sqlAuthDAO.getAuth(makeMoveCommand.getAuthToken()).username(), loadGameMessage);
            }
        } catch (SQLException | DataAccessException | IOException | ResultExceptions exception) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: " + exception.getMessage());
            connections.broadcast(null, errorMessage);
        } catch (InvalidMoveException e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: Invalid move");
            connections.broadcast(null, errorMessage);
        }
    }

    public void resign(UserGameCommand userGameCommand, Session session) {
        //what does resign mean? take game out of database?
    }

    public void leave(UserGameCommand userGameCommand, Session session) throws SQLException, ResultExceptions,
            DataAccessException, IOException {
        int gameId = userGameCommand.getGameID();
        String whiteTeam = sqlGameDAO.getGame(gameId).whiteUsername();
        String blackUsername = sqlGameDAO.getGame(gameId).blackUsername();
        String gameName = sqlGameDAO.getGame(gameId).gameName();
        ChessGame chessGame = sqlGameDAO.getGame(gameId).game();
        String user = sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username();
        String message;
        if (user.equals(sqlGameDAO.getGame(gameId).whiteUsername())) {
            sqlGameDAO.updateGame(gameId, null, blackUsername, gameName, chessGame);
            message = user + "has left the game";
        } else if (user.equals(sqlGameDAO.getGame(gameId).blackUsername())) {
            sqlGameDAO.updateGame(gameId, whiteTeam, null, gameName, chessGame);
            message = user + "has left the game";
        } else {
            message = user + "has stopped observing the game";
        }
        connections.remove(user);
        ServerMessage serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(user, serverMessage);
        String game = new Gson().toJson(chessGame);
        ServerMessage loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.broadcast(null, loadGame);
    }

}
