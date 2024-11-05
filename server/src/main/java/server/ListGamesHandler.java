package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.requests.ListGamesRequest;
import service.ResultExceptions;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;

public class ListGamesHandler implements Route {
    public Service userService = new Service();

    public ListGamesHandler() throws ResultExceptions, DataAccessException {
    }

    public Object handle(Request request, Response response) throws DataAccessException, SQLException {
        var listGameRequest = new ListGamesRequest(request.headers("Authorization"));
        try {
            var listGameResult = userService.listGames(listGameRequest);
            response.status(200);
            response.body(new Gson().toJson(listGameResult));
            return response.body();
        } catch (ResultExceptions e) {
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
