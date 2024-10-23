package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.Requests.ListGamesRequest;
import service.ResultExceptions;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    public UserService userService = new UserService();

    public Object handle(Request request, Response response) throws DataAccessException, ResultExceptions {
        var listGameRequest = new ListGamesRequest(request.headers("Authorization"));
        try {
            var listGameResult = userService.listGames(listGameRequest);
            response.status(200);
            response.body(new Gson().toJson(listGameResult));
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
        }
    }
}
