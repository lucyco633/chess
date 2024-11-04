package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    //map with username as key and UserData object as the value
    public static HashMap<String, UserData> userDB = new HashMap<>();

    @Override
    public UserData getUser(String username) {
        return userDB.get(username);
    }

    @Override
    public void createUser(String username, String password, String email) {
        UserData newUser = new UserData(username, password, email);
        userDB.put(username, newUser);
    }

    @Override
    public void deleteUser(String username) {
        userDB.remove(username);
    }
}
