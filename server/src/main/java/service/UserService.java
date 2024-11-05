package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.requests.*;
import service.results.*;

public class UserService {

    public SqlUserDAO sqlUserDAO = new SqlUserDAO();
    public SqlGameDAO sqlGameDAO = new SqlGameDAO();
    public SqlAuthDAO sqlAuthDAO = new SqlAuthDAO();

    public UserService() throws ResultExceptions, DataAccessException {
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResultExceptions,
            ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError, DataAccessException {
        if (registerRequest == null) {
            throw new ResultExceptions.BadRequestError("{ \"message\": \"Error: bad request\" }");
        }
        if (registerRequest.username() == null || registerRequest.password() == null ||
                registerRequest.email() == null) {
            throw new ResultExceptions.BadRequestError("{ \"message\": \"Error: bad request\" }");
        }
        if (sqlUserDAO.getUser(registerRequest.username()) != null) {
            throw new ResultExceptions.AlreadyTakenError("{ \"message\": \"Error: already taken\" }");
        }
        if (sqlUserDAO.getUser(registerRequest.username()) == null) {
            sqlUserDAO.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
            String authToken = sqlAuthDAO.createAuth(registerRequest.username());
            return new RegisterResult(registerRequest.username(), authToken);
        } else {
            throw new ResultExceptions("{ \"message\": \"Error: unable to register\" }");
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws ResultExceptions.AuthorizationError, ResultExceptions, DataAccessException {
        UserData userData = sqlUserDAO.getUser(loginRequest.username());
        String authToken = "";
        if (userData != null) {
            //changed to check hashed password
            if (BCrypt.checkpw(loginRequest.password(), userData.password())) {
                authToken = sqlAuthDAO.createAuth(loginRequest.username());
                return new LoginResult(loginRequest.username(), authToken);
            }
        }
        if (authToken.equals("")) {
            throw new ResultExceptions.AuthorizationError("{ \"message\": \"Error: unauthorized\" }");
        } else {
            throw new ResultExceptions("{ \"message\": \"Error: unable to login\" }");
        }
    }

    public EmptyResult logout(LogoutRequest logoutRequest) throws DataAccessException,
            ResultExceptions.AuthorizationError, ResultExceptions {
        if (logoutRequest == null) {
            throw new ResultExceptions.AuthorizationError("{ \"message\": \"Error: unauthorized\" }");
        }
        String authToken = logoutRequest.authToken();
        if (sqlAuthDAO.getAuth(authToken) != null) {
            sqlAuthDAO.deleteAuth(authToken);
            return new EmptyResult();
        }
        if (sqlAuthDAO.getAuth(authToken) == null) {
            throw new ResultExceptions.AuthorizationError("{ \"message\": \"Error: unauthorized\" }");
        } else {
            throw new ResultExceptions("{ \"message\": \"Error: unable to logout\" }");
        }
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException,
            ResultExceptions.AuthorizationError, ResultExceptions {
        if (listGamesRequest == null) {
            throw new ResultExceptions.AuthorizationError("{ \"message\": \"Error: unauthorized\" }");
        }
        AuthData authData = sqlAuthDAO.getAuth(listGamesRequest.authToken());
        if (authData == null) {
            throw new ResultExceptions.AuthorizationError("{ \"message\": \"Error: unauthorized\" }");
        }
        if (authData != null) {
            return new ListGamesResult(sqlGameDAO.listGames());
        } else {
            throw new ResultExceptions("{ \"message\": \"Error: unable to list games\" }");
        }
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException,
            ResultExceptions.AuthorizationError, ResultExceptions, ResultExceptions.BadRequestError {
        if (createGameRequest == null) {
            throw new ResultExceptions.BadRequestError("{ \"message\": \"Error: bad request\" }");
        }
        if (sqlAuthDAO.getAuth(createGameRequest.authToken()) != null) {
            GameData newGame = sqlGameDAO.createGame(createGameRequest.gameName());
            return new CreateGameResult(newGame.gameID());
        }
        if (sqlAuthDAO.getAuth(createGameRequest.authToken()) == null) {
            throw new ResultExceptions.AuthorizationError("{ \"message\": \"Error: unauthorized\" }");
        } else {
            throw new ResultExceptions("{ \"message\": \"Error: unable to create game\" }");
        }
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws DataAccessException,
            ResultExceptions.BadRequestError, ResultExceptions.AuthorizationError, ResultExceptions.AlreadyTakenError, ResultExceptions {
        if (joinGameRequest == null) {
            throw new ResultExceptions.BadRequestError("{ \"message\": \"Error: bad request\" }");
        }
        AuthData authData = sqlAuthDAO.getAuth(joinGameRequest.authToken());
        if (authData == null) {
            throw new ResultExceptions.AuthorizationError("{ \"message\": \"Error: unauthorized\" }");
        }
        if (joinGameRequest.playerColor() == null) {
            throw new ResultExceptions.BadRequestError("{ \"message\": \"Error: bad request\" }");
        }
        if (!joinGameRequest.playerColor().equals("WHITE") && !joinGameRequest.playerColor().equals("BLACK")) {
            throw new ResultExceptions.BadRequestError("{ \"message\": \"Error: bad request\" }");
        }
        if (joinGameRequest.gameID() == null) {
            throw new ResultExceptions.BadRequestError("{ \"message\": \"Error: bad request\" }");
        }
        if (authData != null) {
            JoinGameResult joinGameResult = null;
            if ((joinGameRequest.playerColor().equals("BLACK") &&
                    sqlGameDAO.getGame(joinGameRequest.gameID()).blackUsername() != null) ||
                    (joinGameRequest.playerColor().equals("WHITE") &&
                            sqlGameDAO.getGame(joinGameRequest.gameID()).whiteUsername() != null)) {
                throw new ResultExceptions.AlreadyTakenError("{ \"message\": \"Error: already taken\" }");
            }
            if (joinGameRequest.playerColor().equals("BLACK")) {
                sqlGameDAO.updateGame(joinGameRequest.gameID(),
                        sqlGameDAO.getGame(joinGameRequest.gameID()).whiteUsername(),
                        sqlAuthDAO.getAuth(joinGameRequest.authToken()).username(),
                        sqlGameDAO.getGame(joinGameRequest.gameID()).gameName(),
                        sqlGameDAO.getGame(joinGameRequest.gameID()).game());
                joinGameResult = new JoinGameResult(joinGameRequest.playerColor(), joinGameRequest.gameID());
            }
            if (joinGameRequest.playerColor().equals("WHITE")) {
                sqlGameDAO.updateGame(joinGameRequest.gameID(),
                        sqlAuthDAO.getAuth(joinGameRequest.authToken()).username(),
                        sqlGameDAO.getGame(joinGameRequest.gameID()).blackUsername(),
                        sqlGameDAO.getGame(joinGameRequest.gameID()).gameName(),
                        sqlGameDAO.getGame(joinGameRequest.gameID()).game());
                joinGameResult = new JoinGameResult(joinGameRequest.playerColor(), joinGameRequest.gameID());
            }
            return joinGameResult;
        } else {
            throw new ResultExceptions("{ \"message\": \"Error: unable to join game\" }");
        }
    }

    public EmptyResult clear(EmptyRequest emptyRequest) throws DataAccessException,
            ResultExceptions {
        sqlAuthDAO.deleteAllAuth();
        sqlUserDAO.deleteAllUsers();
        sqlGameDAO.deleteAllGames();
        //if (sqlAuthDAO.authDB.isEmpty() && sqlGameDAO.gameDB.isEmpty() && sqlUserDAO.userDB.isEmpty()) {
        //return new EmptyResult();
        //} else {
        //throw new ResultExceptions("{ \"message\": \"Error: unable to clear data\" }");
        //}
        return new EmptyResult();
    }
}
