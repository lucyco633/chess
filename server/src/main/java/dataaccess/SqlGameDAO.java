package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.ResultExceptions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class SqlGameDAO implements GameDAO {

    public SqlGameDAO() throws ResultExceptions, DataAccessException {
        configureDatabase();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException, ResultExceptions {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game, json FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResultExceptions(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException {

    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException, ResultExceptions {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game, json) VALUES (?, ?, ?, ?, ?, ?)";
        Random rand = new Random();
        GameData newGame = new GameData(rand.nextInt(100), null, null, gameName, new ChessGame());
        var json = new Gson().toJson(newGame);
        var gameJson = new Gson().toJson(newGame.game());
        executeCreate(statement, newGame.gameID(), newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), gameJson, json);
        return newGame;
    }

    public void deleteAllGames() throws ResultExceptions {
        var statement = "TRUNCATE game";
        executeDeleteAll(statement);
    }

    public Collection<GameData> listGames() throws ResultExceptions {
        Collection<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        games.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResultExceptions(String.format("Unable to read data: %s", e.getMessage()));
        }
        return games;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        //var id = rs.getInt("id");
        var json = rs.getString("json");
        return new Gson().fromJson(json, GameData.class);
    }

    private void executeCreate(String statement, Object... params) throws ResultExceptions {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResultExceptions(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeDeleteAll(String statement) throws ResultExceptions {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResultExceptions(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final String[] createGameStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` TEXT DEFAULT NULL,
              `json` TEXT DEFAULT NULL,
              INDEX(gameID)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException, ResultExceptions {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createGameStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResultExceptions(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
