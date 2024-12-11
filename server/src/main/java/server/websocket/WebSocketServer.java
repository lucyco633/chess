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

import static server.Server.sqlAuthDAO;
import static server.Server.sqlGameDAO;

@WebSocket
public class WebSocketServer {

    private final ConnectionManager connections = new ConnectionManager();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws SQLException, IOException, ResultExceptions,
            DataAccessException, InvalidMoveException {

        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        MakeMoveCommand makeMoveCommand = null;
        if (userGameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
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
                        "Error: Invalid user token\n");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (sqlGameDAO.getGame(gameId) == null) {
                ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid game ID\n");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else {
                String rootClientUsername = sqlAuthDAO.getAuth(authToken).username();
                connections.add(rootClientUsername, session,
                        sqlGameDAO.getGame(userGameCommand.getGameID()).game(), gameId);
                String message = "";
                if (Objects.equals(rootClientUsername,
                        sqlGameDAO.getGame(userGameCommand.getGameID()).blackUsername())) {
                    message = rootClientUsername + " joined game as team black\n";
                } else if (Objects.equals(sqlAuthDAO.getAuth(userGameCommand.getAuthToken()).username(),
                        sqlGameDAO.getGame(userGameCommand.getGameID()).whiteUsername())) {
                    message = rootClientUsername + " joined game as team white\n";
                } else {
                    message = rootClientUsername + " joined game as observer\n";
                }
                ServerMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                        new Gson().toJson(sqlGameDAO.getGame(userGameCommand.getGameID()).game()));
                connections.sendToClient(rootClientUsername, loadGameMessage, gameId);
                ServerMessage serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(rootClientUsername, serverMessage, gameId);
            }
        } catch (SQLException | ResultExceptions | DataAccessException | IOException e) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: " + e.getMessage() + "\n");
            connections.broadcast(null, errorMessage, userGameCommand.getGameID());
        }
    }

    public boolean checkPlayer(ChessGame chessGame, ChessMove chessMove, String clientUsername, String blackUsername,
                               String whiteUsername) {
        return (Objects.equals(chessGame.getBoard().getPiece(chessMove.getStartPosition()).getTeamColor(),
                ChessGame.TeamColor.WHITE) && Objects.equals(clientUsername, blackUsername)) |
                (Objects.equals(chessGame.getBoard().getPiece(chessMove.getStartPosition()).getTeamColor(),
                        ChessGame.TeamColor.BLACK) && Objects.equals(clientUsername, whiteUsername));
    }

    public boolean checkMove(ChessGame chessGame, ChessMove chessMove, String clientUsername, String blackUsername,
                             String whiteUsername) {
        return (!chessGame.validMoves(chessMove.getStartPosition()).contains(chessMove)
                && !((Objects.equals(chessGame.getBoard().getPiece(chessMove.getStartPosition()).getTeamColor(),
                ChessGame.TeamColor.WHITE) && Objects.equals(clientUsername, blackUsername)) |
                (Objects.equals(chessGame.getBoard().getPiece(chessMove.getStartPosition()).getTeamColor(),
                        ChessGame.TeamColor.BLACK) && Objects.equals(clientUsername, whiteUsername))));
    }

    public boolean checkTurn(ChessGame chessGame, GameData gameData, String clientUsername, String blackUsername,
                             String whiteUsername) {
        return ((chessGame.getTeamTurn().equals(ChessGame.TeamColor.BLACK) && whiteUsername.equals(clientUsername) &&
                !chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) &&
                !chessGame.isInStalemate(ChessGame.TeamColor.WHITE)) |
                (chessGame.getTeamTurn().equals(ChessGame.TeamColor.WHITE) &&
                        Objects.equals(blackUsername, clientUsername)
                        && !chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)
                        && !chessGame.isInStalemate(ChessGame.TeamColor.BLACK))) && !gameData.resigned();
    }

    public void makeMove(MakeMoveCommand makeMoveCommand, Session session) throws IOException {
        try {
            int gameId = makeMoveCommand.getGameID();
            String authToken = makeMoveCommand.getAuthToken();
            ChessMove chessMove = makeMoveCommand.getChessMove();
            if (sqlGameDAO.getGame(gameId) == null) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid game ID\n");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (sqlAuthDAO.getAuth(authToken) == null) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid user token\n");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            }
            GameData gameData = sqlGameDAO.getGame(gameId);
            String whiteUsername = gameData.whiteUsername();
            String blackUsername = gameData.blackUsername();
            String clientUsername = sqlAuthDAO.getAuth(authToken).username();
            ChessGame chessGame = gameData.game();
            if (checkPlayer(chessGame, chessMove, clientUsername, blackUsername, whiteUsername)) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid move, not your player\n");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (checkMove(chessGame, chessMove, clientUsername, blackUsername, whiteUsername)) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid move\n");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (gameData.resigned()) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid move, game over\n");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (checkTurn(chessGame, gameData, clientUsername, blackUsername, whiteUsername)) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid move, not your turn\n");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if ((!Objects.equals(whiteUsername, clientUsername) &&
                    !Objects.equals(blackUsername, clientUsername)) && !gameData.resigned()) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid move, you are observing the game\n");
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
                        "Move was made\n");
                connections.broadcast(clientUsername, serverMessage, gameId);
                if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                    ServerMessage checkmateMessage = new NotificationMessage(
                            ServerMessage.ServerMessageType.NOTIFICATION, blackUsername + "is in checkmate\n");
                    connections.broadcast(clientUsername, checkmateMessage, gameId);
                    connections.sendToClient(clientUsername, checkmateMessage, gameId);
                }
                if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                    ServerMessage checkmateMessage = new NotificationMessage(
                            ServerMessage.ServerMessageType.NOTIFICATION, whiteUsername + "is in checkmate\n");
                    connections.broadcast(clientUsername, checkmateMessage, gameId);
                    connections.sendToClient(clientUsername, checkmateMessage, gameId);
                }
                if (chessGame.isInCheck(ChessGame.TeamColor.BLACK) &&
                        !chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                    ServerMessage checkmateMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            blackUsername + "is in check\n");
                    connections.broadcast(clientUsername, checkmateMessage, gameId);
                    connections.sendToClient(clientUsername, checkmateMessage, gameId);
                }
                if (chessGame.isInCheck(ChessGame.TeamColor.WHITE) &&
                        !chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                    ServerMessage checkmateMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            whiteUsername + "is in check\n");
                    connections.broadcast(clientUsername, checkmateMessage, gameId);
                    connections.sendToClient(clientUsername, checkmateMessage, gameId);
                }
                if (chessGame.isInStalemate(ChessGame.TeamColor.BLACK) |
                        chessGame.isInStalemate(ChessGame.TeamColor.WHITE)) {
                    ServerMessage stalemateMessage = new NotificationMessage(
                            ServerMessage.ServerMessageType.NOTIFICATION, "Stalemate\n");
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
                    "Error: " + exception.getMessage() + "\n");
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
                        "Error: Invalid game ID\n");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (sqlAuthDAO.getAuth(authToken) == null) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Invalid user token\n");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (!sqlAuthDAO.getAuth(authToken).username().equals(sqlGameDAO.getGame(gameId).whiteUsername()) &&
                    !sqlAuthDAO.getAuth(authToken).username().equals(sqlGameDAO.getGame(gameId).blackUsername())) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Cannot resign, you are observing\n");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else if (sqlGameDAO.getGame(gameId).resigned()) {
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                        "Error: Cannot resign, game already resigned\n");
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } else {
                GameData gameData = sqlGameDAO.getGame(gameId);
                sqlGameDAO.updateGame(gameId, gameData.whiteUsername(), gameData.blackUsername(),
                        gameData.gameName(), gameData.game(), true);
                ServerMessage serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        "Game over :(\n");
                connections.broadcast(null, serverMessage, gameId);
            }
        } catch (SQLException | DataAccessException | IOException | ResultExceptions exception) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: " + exception.getMessage() + "\n");
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
            message = user + " has left the game\n";
        } else if (user.equals(sqlGameDAO.getGame(gameId).blackUsername())) {
            sqlGameDAO.updateGame(gameId, whiteTeam, null, gameName, chessGame, false);
            message = user + " has left the game\n";
        } else {
            message = user + " has stopped observing the game\n";
        }
        connections.remove(user, gameId);
        ServerMessage serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(user, serverMessage, gameId);
    }

}
