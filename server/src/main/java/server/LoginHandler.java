package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.requests.LoginRequest;
import service.ResultExceptions;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;

public class LoginHandler implements Route {
    public Service userService = new Service();

    public LoginHandler() throws ResultExceptions, DataAccessException {
    }

    public Object handle(Request request, Response response) throws DataAccessException, SQLException {
        var loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
        try {
            var loginResult = userService.login(loginRequest);
            response.status(200);
            response.body(new Gson().toJson(loginResult));
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
