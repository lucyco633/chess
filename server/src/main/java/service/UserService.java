package service;

import dataaccess.DataAccessException;
import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import model.GameData;

public class UserService {
    private final AuthDataAccess authDataAccess;
    private final UserDataAccess userDataAccess;
    private final GameDataAccess gameDataAccess;

    public UserService(AuthDataAccess authDataAccess, UserDataAccess userDataAccess, GameDataAccess gameDataAccess) {
        this.authDataAccess = authDataAccess;
        this.userDataAccess = userDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    //public AuthData register(UserData user) {}
    //public AuthData login(UserData user) {}
    //public void logout(AuthData auth) {}
    //public Map<Integer, GameData> listGames() {}
    //public String createGame(String gameName) {}
    //public void joinGame(UserData user) {}

    public void clear() throws DataAccessException {
        authDataAccess.deleteAllAuthData();
        userDataAccess.deleteAllUserData();
        gameDataAccess.deleteAllGameData();
    }
}
