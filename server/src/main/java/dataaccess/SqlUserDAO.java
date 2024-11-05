package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.ResultExceptions;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlUserDAO implements UserDAO {

    public SqlUserDAO() throws ResultExceptions, DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData getUser(String username) throws ResultExceptions {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email, json FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResultExceptions(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void createUser(String username, String password, String email) throws ResultExceptions, SQLException, DataAccessException {
        var statement = "INSERT INTO user (username, password, email, json) VALUES (?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        UserData newUser = new UserData(username, hashedPassword, email);
        var json = new Gson().toJson(newUser);
        executeCreate(statement, newUser.username(), newUser.password(), newUser.email(), json);
    }

    public void deleteAllUsers() throws ResultExceptions, SQLException, DataAccessException {
        var statement = "TRUNCATE user";
        executeDeleteAll(statement);
    }

    private void executeCreate(String statement, Object... params) throws DataAccessException, SQLException {
        var conn = DatabaseManager.getConnection();
        try (var ps = conn.prepareStatement(statement)) {
            for (var i = 0; i < params.length; i++) {
                var param = params[i];
                if (param instanceof String p) {
                    ps.setString(i + 1, p);
                }
            }
            ps.executeUpdate();
        }

    }

    private void executeDeleteAll(String statement) throws DataAccessException, SQLException {
        var conn = DatabaseManager.getConnection();
        try (var ps = conn.prepareStatement(statement)) {
            ps.executeUpdate();
        }
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, UserData.class);
    }

    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException, ResultExceptions {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createUserStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResultExceptions(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
