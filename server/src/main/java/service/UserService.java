package service;

import dataaccess.*;
import model.UserData;

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

    public void logout(AuthData auth) {}
    //public Map<Integer, GameData> listGames() {}
    //public String createGame(String gameName) {}
    //public void joinGame(UserData user) {}

    public void clear() throws DataAccessException {
        //clear all DBs
    }
}
