package service;

import dataaccess.*;

public class UserService {

    public DataAccess dataAccess;

    //can pass in UserData or RegisterRequest
    //return Register Result?
    public void register(String username, String password, String email) throws DataAccessException{
        //public boolean?
        if (dataAccess.getUser(username) == null){
            dataAccess.createUser(username, password, email);
        }
    }
    //public AuthData login(UserData user) {}
    //public void logout(AuthData auth) {}
    //public Map<Integer, GameData> listGames() {}
    //public String createGame(String gameName) {}
    //public void joinGame(UserData user) {}

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }
}
