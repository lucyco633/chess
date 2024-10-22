package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Map;

public class UserService {

    public MemoryUserDAO memoryUserDAO;
    public MemoryGameDAO memoryGameDAO;
    public MemoryAuthDAO memoryAuthDAO;

    //can pass in UserData or RegisterRequest
    //return Register Result?
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException{
        //public boolean?
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null){
            //{ "message": "Error: bad request" }
        }
        if (memoryUserDAO.getUser(registerRequest.username()) != null){
            //{ "message": "Error: already taken" }
        }
        if (memoryUserDAO.getUser(registerRequest.username()) == null){
            memoryUserDAO.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
            String authToken = memoryAuthDAO.createAuth(registerRequest.username());
            RegisterResult registerResult = new RegisterResult(registerRequest.username(), authToken);
            return registerResult;
        }
        else {
            //{ "message": "Error: (description of error)" }
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        UserData userData = memoryUserDAO.getUser(loginRequest.username());
        String authToken = "";
        if (userData != null){
            if (userData.password() == loginRequest.password()){
                authToken = memoryAuthDAO.createAuth(loginRequest.username());
                return new LoginResult(loginRequest.username(), authToken);
            }
        }
        if (authToken == "") {
            //{ "message": "Error: unauthorized" }
        }
        //return { "message": "Error: (description of error)" }
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException{
        String authToken = logoutRequest.authToken();
        if (memoryAuthDAO.getAuth(authToken) == null){
            //{ "message": "Error: unauthorized" }
        }
        if (memoryAuthDAO.getAuth(authToken) != null){
            memoryAuthDAO.deleteAuth(authToken);
            //{}
        }
        else {
            //{ "message": "Error: (description of error)" }
        }
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        AuthData authData = memoryAuthDAO.getAuth(listGamesRequest.authToken());
        if (authData == null) {
            //{ "message": "Error: unauthorized" }
        }
        if (authData != null){
            return new ListGamesResult(memoryGameDAO.gameDB);
        }
        //{ "message": "Error: (description of error)" }
    }

    public  CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        //if request bad? { "message": "Error: bad request" }
        if (memoryAuthDAO.getAuth(createGameRequest.authToken()) == null){
            //{ "message": "Error: unauthorized" }
        }
        if (memoryAuthDAO.getAuth(createGameRequest.authToken()) != null){
            GameData newGame = memoryGameDAO.createGame(createGameRequest.gameName());
            return new CreateGameResult(newGame.gameID());
        }
        //500 { "message": "Error: (description of error)" }
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws DataAccessException{
        //if bad request
        AuthData authData = memoryAuthDAO.getAuth(joinGameRequest.authToken());
        if (authData == null){
            //401 { "message": "Error: unauthorized" }
        }
        if (authData != null){
            if ((joinGameRequest.playerColor() == "BLACK" && memoryGameDAO.getGame(joinGameRequest.gameID()).blackUsername() != null) ||
                    (joinGameRequest.playerColor() == "WHITE" && memoryGameDAO.getGame(joinGameRequest.gameID()).whiteUsername() != null)) {
                //{ "message": "Error: already taken" }
            }
            if (joinGameRequest.playerColor() == "BLACK"){
                memoryGameDAO.updateGame(joinGameRequest.gameID(),
                        null,
                        memoryAuthDAO.getAuth(joinGameRequest.authToken()).username(),
                        memoryGameDAO.getGame(joinGameRequest.gameID()).gameName(),
                        memoryGameDAO.getGame(joinGameRequest.gameID()).game());
                return new JoinGameResult(joinGameRequest.playerColor(), joinGameRequest.gameID());
            }
            if (joinGameRequest.playerColor() == "WHITE"){
                memoryGameDAO.updateGame(joinGameRequest.gameID(),
                        memoryAuthDAO.getAuth(joinGameRequest.authToken()).username(),
                        null,
                        memoryGameDAO.getGame(joinGameRequest.gameID()).gameName(),
                        memoryGameDAO.getGame(joinGameRequest.gameID()).game());
                return new JoinGameResult(joinGameRequest.playerColor(), joinGameRequest.gameID());
            }
        }
        //{ "message": "Error: (description of error)" }

    }

    public void clear() throws DataAccessException {
        memoryAuthDAO.authDB.clear();
        memoryUserDAO.userDB.clear();
        memoryGameDAO.gameDB.clear();
        if (!memoryAuthDAO.authDB.isEmpty() || !memoryGameDAO.gameDB.isEmpty() || !memoryUserDAO.userDB.isEmpty()){
            //500 {Error: Data not cleared}
        }
        //200 {}
    }
}
