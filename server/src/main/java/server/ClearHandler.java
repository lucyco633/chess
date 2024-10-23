package server;

import java.io.*;
import java.net.*;

import com.google.gson.Gson;
import com.sun.net.httpserver.*;
import dataaccess.DataAccessException;
import service.Requests.EmptyRequest;
import service.ResultExceptions;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;


public class ClearHandler implements Route {

    public UserService userService = new UserService();

    public Object handle(Request request, Response response) throws DataAccessException, ResultExceptions {
        var emptyRequest = new Gson().fromJson(request.body(), EmptyRequest.class);
        try {
            var emptyResult = userService.clear(emptyRequest);
            response.status(200);
            response.body("{}");
            return "{}";
        }
        catch (ResultExceptions e){
            //add string??
            response.status(500);
            response.body("{ \"message\": \"Error: unable to clear game\" }");
            return response.body();
        }
    }
}
