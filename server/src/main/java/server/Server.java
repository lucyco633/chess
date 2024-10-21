package server;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import model.AuthData;
import dataaccess.DataAccessException;
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
        //Spark.delete("/db", this::clearAllData);
        Spark.delete("/db", (req, res) ->
                (new ClearHandler()).handle(req, res));


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    //private Object clearAllData(Request req, Response res) throws DataAccessException {
        //userService.clear();
        //res.status(200);
        //return "";
    //}

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
