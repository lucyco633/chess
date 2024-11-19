package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.requests.CreateGameRequest;
import service.ResultExceptions;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;


public class CreateGameHandler implements Route {
    public Service userService = new Service();

    public CreateGameHandler() throws ResultExceptions, DataAccessException {
    }

    public Object handle(Request request, Response response) throws DataAccessException, SQLException {
        var gameName = new Gson().fromJson(request.body(), GameName.class);
        var createGameRequest = new CreateGameRequest(gameName.gameName(), request.headers("Authorization"));
        try {
            var createGameResult = userService.createGame(createGameRequest);
            response.status(200);
            response.body(new Gson().toJson(createGameResult));
            return response.body();
        } catch (ResultExceptions e) {
            response.status(500);
            response.body(new Gson().toJson(new ErrorMessages("Error: unable to create game")));
            return response.body();
        } catch (ResultExceptions.AuthorizationError e) {
            response.status(401);
            response.body(new Gson().toJson(new ErrorMessages("Error: unauthorized")));
            return response.body();
        } catch (ResultExceptions.BadRequestError e) {
            response.status(400);
            response.body(new Gson().toJson(new ErrorMessages("Error: bad request")));
            return response.body();
        }
    }
}
