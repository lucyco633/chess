package dataaccess;
//create model class with record for User? Where?
import model.UserData;


public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;

    void createUser(String username, String password, String email) throws DataAccessException;

    void updateUser(UserData user, String password, String email);

    void deleteUser(String username) throws DataAccessException;

}
