package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.requests.RegisterRequest;
import service.ResultExceptions;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.SQLException;

public class RegisterHandler implements Route {
    public Service userService = new Service();

    public RegisterHandler() throws ResultExceptions, DataAccessException {
    }

    public Object handle(Request request, Response response) throws DataAccessException, SQLException {
        var registerRequest = new Gson().fromJson(request.body(), RegisterRequest.class);
        try {
            var registerResult = userService.register(registerRequest);
            response.status(200);
            response.body(new Gson().toJson(registerResult));
            return response.body();
        } catch (ResultExceptions e) {
            response.status(500);
            response.body(new Gson().toJson(new ErrorMessages("Error: unable to register")));
            return response.body();
        } catch (ResultExceptions.BadRequestError e) {
            response.status(400);
            response.body(new Gson().toJson(new ErrorMessages("Error: bad request")));
            return response.body();
        } catch (ResultExceptions.AlreadyTakenError e) {
            response.status(403);
            response.body(new Gson().toJson(new ErrorMessages("Error: already taken")));
            return response.body();
        }
    }
}
