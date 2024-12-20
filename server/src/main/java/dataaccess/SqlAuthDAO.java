package dataaccess;

import model.AuthData;
import service.ResultExceptions;

import java.sql.SQLException;

import java.sql.*;
import java.util.UUID;


public class SqlAuthDAO implements AuthDAO {

    public SqlAuthDAO() throws ResultExceptions, DataAccessException {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, ResultExceptions, SQLException {
        var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return readAuth(rs);
                }
            }
        }
        return null;
    }


    @Override
    public String createAuth(String username) throws DataAccessException, ResultExceptions, SQLException {
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        UpdateSql.executeUpdate(statement, newAuth.authToken(), newAuth.username());
        return newAuth.authToken();
    }


    @Override
    public void deleteAuth(String authToken) throws DataAccessException, SQLException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        UpdateSql.executeUpdate(statement, authToken);
    }

    public void deleteAllAuth() throws SQLException, DataAccessException {
        var statement = "TRUNCATE auth";
        executeDeleteAll(statement);
    }


    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private void executeDeleteAll(String statement) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
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
