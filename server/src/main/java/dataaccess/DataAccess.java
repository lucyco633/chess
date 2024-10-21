package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public class DataAccess {

    public MemoryAuthDAO memoryAuthDAO;
    public MemoryGameDAO memoryGameDAO;
    public MemoryUserDAO memoryUserDAO;

    public void clear() throws DataAccessException{
        memoryAuthDAO.authDB.clear();
        memoryGameDAO.gameDB.clear();
        memoryUserDAO.userDB.clear();
    }

    public void createUser(String username, String password, String email) throws DataAccessException {
        memoryUserDAO.createUser(username, password, email);
    }

    public UserData getUser(String username) throws DataAccessException {
        return memoryUserDAO.getUser(username);
    }

    public void createGame(String gameName) throws DataAccessException {
        //how to set arbitrary values??
        memoryGameDAO.createGame(gameName);
    }

    public GameData getGame(Integer gameID) throws DataAccessException {
        return memoryGameDAO.getGame(gameID);
    }

    public HashMap<Integer, GameData> listGames() throws DataAccessException {
        //return map? or list of values in map?
        return memoryGameDAO.gameDB;
    }

    public void updateGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException {
        memoryGameDAO.updateGame(gameID, whiteUsername, blackUsername, gameName, game);
    }

    public void createAuth(String username) throws DataAccessException {
        memoryAuthDAO.createAuth(username);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return memoryAuthDAO.getAuth(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        memoryAuthDAO.deleteAuth(authToken);
    }


}
