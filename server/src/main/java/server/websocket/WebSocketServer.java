package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
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
            int gameId = userGameCommand.getGameID();
            String authToken = userGameCommand.getAuthToken();
            if (sqlAuthDAO.getAuth(authToken) == null) {
                ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid user token");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (sqlGameDAO.getGame(gameId) == null) {
                ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid game ID");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else {
                String rootClientUsername = sqlAuthDAO.getAuth(authToken).username();
                connections.add(rootClientUsername, session,
                        sqlGameDAO.getGame(userGameCommand.getGameID()).game(), gameId);
                String message = sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username() +
                        " joined game as observer";
                if (Objects.equals(sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username(),
                        sqlGameDAO.getGame(userGameCommand.getGameID()).blackUsername())) {
                    message = sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username() +
                            " joined game as team black";
                } else if (Objects.equals(sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username(),
                        sqlGameDAO.getGame(userGameCommand.getGameID()).whiteUsername())) {
                    message = sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username() +
                            " joined game as team white";
                }
                ServerMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                        new Gson().toJson(sqlGameDAO.getGame(userGameCommand.getGameID()).game()));
                connections.sendToClient(rootClientUsername, loadGameMessage, gameId);
                ServerMessage serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(rootClientUsername, serverMessage, gameId);
            }
        } catch (SQLException | ResultExceptions | DataAccessException | IOException e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: " + e.getMessage());
            connections.broadcast(null, errorMessage, userGameCommand.getGameID());
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
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (sqlAuthDAO.getAuth(authToken) == null) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid user token");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (!sqlGameDAO.getGame(gameId).game().validMoves(chessMove.getStartPosition()).contains(chessMove)) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid move");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (sqlGameDAO.getGame(gameId).resigned()) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid move, game over");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            }
            GameData gameData = sqlGameDAO.getGame(gameId);
            String whiteUsername = gameData.whiteUsername();
            String blackUsername = gameData.blackUsername();
            String clientUsername = sqlAuthDAO.getAuth(authToken).username();
            ChessGame chessGame = gameData.game();
            if (((chessGame.getTeamTurn().equals(ChessGame.TeamColor.BLACK) &&
                    whiteUsername.equals(clientUsername) &&
                    !chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) &&
                    !chessGame.isInStalemate(ChessGame.TeamColor.WHITE)) |
                    (chessGame.getTeamTurn().equals(ChessGame.TeamColor.WHITE) &&
                            blackUsername.equals(clientUsername)
                            && !chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)
                            && !chessGame.isInStalemate(ChessGame.TeamColor.BLACK))) && !gameData.resigned()) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid move, not your turn");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if ((!whiteUsername.equals(clientUsername) &&
                    !blackUsername.equals(clientUsername)) && !gameData.resigned()) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid move, you are observing the game");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (!gameData.resigned()) {
                chessGame.makeMove(makeMoveCommand.getChessMove());
                if (clientUsername.equals(whiteUsername)) {
                    chessGame.setTeamTurn(ChessGame.TeamColor.BLACK);
                } else if (clientUsername.equals(blackUsername)) {
                    chessGame.setTeamTurn(ChessGame.TeamColor.WHITE);
                }
                sqlGameDAO.updateGame(gameId, whiteUsername, blackUsername, gameData.gameName(), chessGame,
                        false);
                ServerMessage serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        "Move was made");
                connections.broadcast(clientUsername, serverMessage, gameId);
                if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                    ServerMessage checkmateMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            blackUsername + "is in checkmate");
                    connections.broadcast(clientUsername, checkmateMessage, gameId);
                    connections.sendToClient(clientUsername, checkmateMessage, gameId);
                }
                if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                    ServerMessage checkmateMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            whiteUsername + "is in checkmate");
                    connections.broadcast(clientUsername, checkmateMessage, gameId);
                    connections.sendToClient(clientUsername, checkmateMessage, gameId);
                }
                if (chessGame.isInCheck(ChessGame.TeamColor.BLACK)) {
                    ServerMessage checkmateMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            blackUsername + "is in check");
                    connections.broadcast(clientUsername, checkmateMessage, gameId);
                    connections.sendToClient(clientUsername, checkmateMessage, gameId);
                }
                if (chessGame.isInCheck(ChessGame.TeamColor.WHITE)) {
                    ServerMessage checkmateMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            whiteUsername + "is in check");
                    connections.broadcast(clientUsername, checkmateMessage, gameId);
                    connections.sendToClient(clientUsername, checkmateMessage, gameId);
                }
                if (chessGame.isInStalemate(ChessGame.TeamColor.BLACK) |
                        chessGame.isInStalemate(ChessGame.TeamColor.WHITE)) {
                    ServerMessage stalemateMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            "Stalemate");
                    connections.broadcast(clientUsername, stalemateMessage, gameId);
                    connections.sendToClient(clientUsername, stalemateMessage, gameId);
                }
                ServerMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                        new Gson().toJson(chessGame));
                connections.broadcast(clientUsername, loadGameMessage, gameId);
                connections.sendToClient(clientUsername, loadGameMessage, gameId);
            }
        } catch (SQLException | DataAccessException | IOException | ResultExceptions exception) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: " + exception.getMessage());
            connections.broadcast(null, errorMessage, makeMoveCommand.getGameID());
        } catch (InvalidMoveException e) {
        }
    }

    public void resign(UserGameCommand userGameCommand, Session session) throws IOException {
        try {
            int gameId = userGameCommand.getGameID();
            String authToken = userGameCommand.getAuthToken();
            if (sqlGameDAO.getGame(gameId) == null) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid game ID");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (sqlAuthDAO.getAuth(authToken) == null) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid user token");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (!sqlAuthDAO.getAuth(authToken).username().equals(sqlGameDAO.getGame(gameId).whiteUsername()) &&
                    !sqlAuthDAO.getAuth(authToken).username().equals(sqlGameDAO.getGame(gameId).blackUsername())) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Cannot resign, you are observing");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (sqlGameDAO.getGame(gameId).resigned()) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Cannot resign, game already resigned");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else {
                GameData gameData = sqlGameDAO.getGame(gameId);
                sqlGameDAO.updateGame(gameId, gameData.whiteUsername(), gameData.blackUsername(),
                        gameData.gameName(), gameData.game(), true);
                ServerMessage serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        "Game over :(");
                connections.broadcast(null, serverMessage, gameId);
            }
        } catch (SQLException | DataAccessException | IOException | ResultExceptions exception) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: " + exception.getMessage());
            connections.broadcast(null, errorMessage, userGameCommand.getGameID());
        }
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
            sqlGameDAO.updateGame(gameId, null, blackUsername, gameName, chessGame, false);
            message = user + "has left the game";
        } else if (user.equals(sqlGameDAO.getGame(gameId).blackUsername())) {
            sqlGameDAO.updateGame(gameId, whiteTeam, null, gameName, chessGame, false);
            message = user + "has left the game";
        } else {
            message = user + "has stopped observing the game";
        }
        connections.remove(user, gameId);
        ServerMessage serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(user, serverMessage, gameId);
    }

}
