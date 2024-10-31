package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.requests.*;
import service.results.*;

import java.util.Objects;

public class UserService {

    public MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    public MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    public MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();

    public RegisterResult register(RegisterRequest registerRequest) throws ResultExceptions,
            ResultExceptions.BadRequestError, ResultExceptions.AlreadyTakenError {
        if (registerRequest == null) {
            throw new ResultExceptions.BadRequestError("{ \"message\": \"Error: bad request\" }");
        }
        if (registerRequest.username() == null || registerRequest.password() == null ||
                registerRequest.email() == null) {
            throw new ResultExceptions.BadRequestError("{ \"message\": \"Error: bad request\" }");
        }
        if (memoryUserDAO.getUser(registerRequest.username()) != null) {
            throw new ResultExceptions.AlreadyTakenError("{ \"message\": \"Error: already taken\" }");
        }
        if (memoryUserDAO.getUser(registerRequest.username()) == null) {
            memoryUserDAO.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
            String authToken = memoryAuthDAO.createAuth(registerRequest.username());
            return new RegisterResult(registerRequest.username(), authToken);
        } else {
            throw new ResultExceptions("{ \"message\": \"Error: unable to register\" }");
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws ResultExceptions.AuthorizationError, ResultExceptions {
        UserData userData = memoryUserDAO.getUser(loginRequest.username());
        String authToken = "";
        if (userData != null) {
            if (Objects.equals(userData.password(), loginRequest.password())) {
                authToken = memoryAuthDAO.createAuth(loginRequest.username());
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
        if (memoryAuthDAO.getAuth(authToken) != null) {
            memoryAuthDAO.deleteAuth(authToken);
            return new EmptyResult();
        }
        if (memoryAuthDAO.getAuth(authToken) == null) {
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
        AuthData authData = memoryAuthDAO.getAuth(listGamesRequest.authToken());
        if (authData == null) {
            throw new ResultExceptions.AuthorizationError("{ \"message\": \"Error: unauthorized\" }");
        }
        if (authData != null) {
            return new ListGamesResult(memoryGameDAO.gameDB.values());
        } else {
            throw new ResultExceptions("{ \"message\": \"Error: unable to list games\" }");
        }
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException,
            ResultExceptions.AuthorizationError, ResultExceptions, ResultExceptions.BadRequestError {
        if (createGameRequest == null) {
            throw new ResultExceptions.BadRequestError("{ \"message\": \"Error: bad request\" }");
        }
        if (memoryAuthDAO.getAuth(createGameRequest.authToken()) != null) {
            GameData newGame = memoryGameDAO.createGame(createGameRequest.gameName());
            return new CreateGameResult(newGame.gameID());
        }
        if (memoryAuthDAO.getAuth(createGameRequest.authToken()) == null) {
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
        AuthData authData = memoryAuthDAO.getAuth(joinGameRequest.authToken());
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
                    memoryGameDAO.getGame(joinGameRequest.gameID()).blackUsername() != null) ||
                    (joinGameRequest.playerColor().equals("WHITE") &&
                            memoryGameDAO.getGame(joinGameRequest.gameID()).whiteUsername() != null)) {
                throw new ResultExceptions.AlreadyTakenError("{ \"message\": \"Error: already taken\" }");
            }
            if (joinGameRequest.playerColor().equals("BLACK")) {
                memoryGameDAO.updateGame(joinGameRequest.gameID(),
                        memoryGameDAO.getGame(joinGameRequest.gameID()).whiteUsername(),
                        memoryAuthDAO.getAuth(joinGameRequest.authToken()).username(),
                        memoryGameDAO.getGame(joinGameRequest.gameID()).gameName(),
                        memoryGameDAO.getGame(joinGameRequest.gameID()).game());
                joinGameResult = new JoinGameResult(joinGameRequest.playerColor(), joinGameRequest.gameID());
            }
            if (joinGameRequest.playerColor().equals("WHITE")) {
                memoryGameDAO.updateGame(joinGameRequest.gameID(),
                        memoryAuthDAO.getAuth(joinGameRequest.authToken()).username(),
                        memoryGameDAO.getGame(joinGameRequest.gameID()).blackUsername(),
                        memoryGameDAO.getGame(joinGameRequest.gameID()).gameName(),
                        memoryGameDAO.getGame(joinGameRequest.gameID()).game());
                joinGameResult = new JoinGameResult(joinGameRequest.playerColor(), joinGameRequest.gameID());
            }
            return joinGameResult;
        } else {
            throw new ResultExceptions("{ \"message\": \"Error: unable to join game\" }");
        }
    }

    public EmptyResult clear(EmptyRequest emptyRequest) throws DataAccessException,
            ResultExceptions {
        memoryAuthDAO.authDB.clear();
        memoryUserDAO.userDB.clear();
        memoryGameDAO.gameDB.clear();
        if (memoryAuthDAO.authDB.isEmpty() && memoryGameDAO.gameDB.isEmpty() && memoryUserDAO.userDB.isEmpty()) {
            return new EmptyResult();
        } else {
            throw new ResultExceptions("{ \"message\": \"Error: unable to clear data\" }");
        }
    }
}
