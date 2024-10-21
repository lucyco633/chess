package service;

import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class UserService {
    private final AuthDAO authDataAccess;
    private final UserDAO userDataAccess;
    private final GameDAO gameDataAccess;

    public UserService(AuthDAO authDataAccess, UserDAO userDataAccess, GameDAO gameDataAccess) {
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
        userDataAccess.deleteUser();
        gameDataAccess.deleteGame();
    }
}
