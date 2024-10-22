package server;

import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
import dataaccess.DataAccessException;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;


public class ClearHandler implements Route {

    public UserService userService;

    public Object handle(Request request, Response response) throws DataAccessException{
        userService.clear();
        //if user.clear() worked then 200, else 500??

        response.status(200);
        return "";
    }
}
