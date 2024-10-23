package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.Requests.CreateGameRequest;
import service.Requests.EmptyRequest;
import service.Requests.LoginRequest;
import service.ResultExceptions;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.lang.reflect.Type;

public class CreateGameHandler implements Route {
    public UserService userService = new UserService();

    public Object handle(Request request, Response response) throws DataAccessException, ResultExceptions {
        var gameName = new Gson().fromJson(request.body(), GameName.class);
        var createGameRequest = new CreateGameRequest(request.headers("Authorization"), gameName.gameName());
        try {
            var createGameResult = userService.createGame(createGameRequest);
            response.status(200);
            response.body(new Gson().toJson(createGameResult));
            return response.body();
        }
        catch (ResultExceptions e){
            response.status(500);
            response.body("{ \"message\": \"Error: unable to create game\" }");
            return response.body();
        } catch (ResultExceptions.AuthorizationError e) {
            response.status(401);
            response.body("{ \"message\": \"Error: unauthorized\" }");
            return response.body();
        } catch (ResultExceptions.BadRequestError e) {
            response.status(400);
            response.body("{ \"message\": \"Error: bad request\" }");
            return response.body();
        }
    }
}
