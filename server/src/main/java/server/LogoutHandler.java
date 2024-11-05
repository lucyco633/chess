package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.requests.LogoutRequest;
import service.ResultExceptions;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;

public class LogoutHandler implements Route {
    public Service userService = new Service();

    public LogoutHandler() throws ResultExceptions, DataAccessException {
    }

    public Object handle(Request request, Response response) throws DataAccessException, SQLException {
        var logoutRequest = new LogoutRequest(request.headers("Authorization"));
        try {
            var logoutResult = userService.logout(logoutRequest);
            response.status(200);
            response.body(new Gson().toJson(logoutResult));
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
