package dataaccess;
//create model class with record for User? Where?

import model.UserData;
import service.ResultExceptions;

import java.sql.SQLException;


public interface UserDAO {
    UserData getUser(String username) throws DataAccessException, ResultExceptions;

    void createUser(String username, String password, String email) throws DataAccessException, ResultExceptions, SQLException;


}
