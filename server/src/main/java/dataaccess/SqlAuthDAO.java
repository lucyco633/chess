package dataaccess;

import com.google.gson.Gson;
import model.AuthData;
import service.ResultExceptions;

import java.sql.SQLException;

import java.sql.*;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;


public class SqlAuthDAO implements AuthDAO {

    public SqlAuthDAO() throws ResultExceptions, DataAccessException {
        configureDatabase();
    }


    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, ResultExceptions {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResultExceptions(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }


    @Override
    public String createAuth(String username) throws DataAccessException, ResultExceptions {
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeCreate(statement, newAuth.authToken(), newAuth.username());
        return newAuth.authToken();
    }


    @Override
    public void deleteAuth(String authToken) throws DataAccessException, ResultExceptions {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeDelete(statement, authToken);
    }

    public void deleteAllAuth() throws ResultExceptions {
        var statement = "TRUNCATE auth";
        executeDeleteAll(statement);
    }


    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private void executeDelete(String statement, Object... params) throws ResultExceptions {
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

    private final String[] createAuthStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY(authToken)
            )
            """
    };

    private void configureDatabase() throws DataAccessException, ResultExceptions {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createAuthStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResultExceptions(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}