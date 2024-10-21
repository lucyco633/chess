package dataaccess;
//create model class with record for User? Where?
import model.UserData;


public interface UserDataAccess {
    UserData getUser(String username) throws DataAccessException;

    UserData createUser(UserData user) throws DataAccessException;

    void deleteAllUserData() throws DataAccessException;

}
