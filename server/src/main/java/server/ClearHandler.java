package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.requests.EmptyRequest;
import service.ResultExceptions;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;


public class ClearHandler implements Route {

    public UserService userService = new UserService();

    public Object handle(Request request, Response response) throws DataAccessException, ResultExceptions {
        var emptyRequest = new Gson().fromJson(request.body(), EmptyRequest.class);
        try {
            var emptyResult = userService.clear(emptyRequest);
            response.status(200);
            response.body("{}");
            return "{}";
        } catch (ResultExceptions e) {
            response.status(500);
            response.body("{ \"message\": \"Error: unable to clear game\" }");
            return response.body();
        }
    }
}
