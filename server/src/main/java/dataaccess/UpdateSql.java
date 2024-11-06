package dataaccess;

import java.sql.SQLException;

public class UpdateSql {

    public static void executeUpdate(String statement, Object... params) throws DataAccessException, SQLException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            for (var i = 0; i < params.length; i++) {
                var param = params[i];
                if (param instanceof String p) {
                    ps.setString(i + 1, p);
                }
            }
            ps.executeUpdate();
        }
    }
}
