package server;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import model.AuthData;
import dataaccess.DataAccessException;
import service.UserService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> new ClearHandler().handle(req, res));
        Spark.post("/game", (req, res) -> new CreateGameHandler().handle(req, res));
        Spark.put("/game", (req, res) -> new JoinGameHandler().handle(req, res));
        Spark.get("/game", (req, res) -> new ListGamesHandler().handle(req, res));
        Spark.post("/session", (req, res) -> new LoginHandler().handle(req, res));
        Spark.delete("/session", (req, res) -> new LogoutHandler().handle(req, res));
        Spark.post("/user", (req, res) -> new RegisterHandler().handle(req, res));

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
