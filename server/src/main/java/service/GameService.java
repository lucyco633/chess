package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class GameService {
    private final AuthDAO authDataAccess;
    private final UserDAO userDataAccess;
    private final GameDAO gameDataAccess;

    public GameService(AuthDAO authDataAccess, UserDAO userDataAccess, GameDAO gameDataAccess) {
        this.authDataAccess = authDataAccess;
        this.userDataAccess = userDataAccess;
        this.gameDataAccess = gameDataAccess;
    }
    //public Map<Integer, GameData> listGames() {}
    //public String createGame(String gameName) {}
    //public void joinGame(UserData user) {}
}
