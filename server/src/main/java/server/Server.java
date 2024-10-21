package server;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import model.AuthData;
import dataaccess.DataAccessException;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private final UserService userService;

    public Server(UserService userService){
        this.userService = userService;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
