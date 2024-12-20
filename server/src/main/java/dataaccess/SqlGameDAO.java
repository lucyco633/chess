package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import service.ResultExceptions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static java.sql.Types.NULL;

public class SqlGameDAO implements GameDAO {

    public SqlGameDAO() throws ResultExceptions, DataAccessException {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws ResultExceptions {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game, resigned, json FROM game WHERE gameID=?";
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
    public void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game, boolean resigned)
            throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement
                    ("UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, game=?, resigned=?, json=? WHERE gameID=?")) {
                preparedStatement.setString(1, whiteUsername);
                preparedStatement.setString(2, blackUsername);
                preparedStatement.setString(3, gameName);
                var jsonGame = new Gson().toJson(game);
                preparedStatement.setString(4, jsonGame);
                preparedStatement.setBoolean(5, resigned);
                var json = new Gson().toJson(new GameData(gameID, whiteUsername, blackUsername, gameName, game, resigned));
                preparedStatement.setString(6, json);
                preparedStatement.setInt(7, gameID);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException, ResultExceptions, SQLException {
        var statement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game, resigned) VALUES (?, ?, ?, ?, ?, ?)";
        Random rand = new Random();
        GameData newGame = new GameData(rand.nextInt(100), null, null,
                gameName, new ChessGame(), false);
        var gameJson = new Gson().toJson(newGame.game());
        executeCreate(statement, newGame.gameID(), null, null, newGame.gameName(), gameJson, false);
        return newGame;
    }

    public void deleteAllGames() throws SQLException, DataAccessException {
        var statement = "TRUNCATE game";
        executeDeleteAll(statement);
    }

    public Collection<GameData> listGames() throws DataAccessException, SQLException {
        Collection<GameData> games = new ArrayList<>();
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game, resigned FROM game";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    games.add(readGame(rs));
                }
            }
        }

        return games;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String gameJson = rs.getString("game");
        boolean resigned = rs.getBoolean("resigned");
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game, resigned);
    }

    private void executeCreate(String statement, Object... params) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            for (var i = 0; i < params.length; i++) {
                var param = params[i];
                if (param instanceof String p) {
                    ps.setString(i + 1, p);
                } else if (param instanceof Integer p) {
                    ps.setInt(i + 1, p);
                } else if (param == null) {
                    ps.setNull(i + 1, NULL);
                } else if (param instanceof Boolean p) {
                    ps.setBoolean(i + 1, p);
                }
            }
            ps.executeUpdate();
        }

    }

    private void executeDeleteAll(String statement) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        }
    }

    private final String[] createGameStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) NULL DEFAULT NULL,
              `blackUsername` varchar(256) NULL DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` TEXT DEFAULT NULL,
              `resigned` BOOLEAN DEFAULT FALSE,
              `json` TEXT DEFAULT NULL,
              INDEX(gameID)
            )
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
