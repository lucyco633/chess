package server;

import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import dataaccess.SqlUserDAO;
import server.websocket.WebSocketServer;
import service.ResultExceptions;
import spark.*;

public class Server {
    public static SqlUserDAO sqlUserDAO;

    static {
        try {
            sqlUserDAO = new SqlUserDAO();
        } catch (ResultExceptions e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static SqlGameDAO sqlGameDAO;

    static {
        try {
            sqlGameDAO = new SqlGameDAO();
        } catch (ResultExceptions e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static SqlAuthDAO sqlAuthDAO;

    static {
        try {
            sqlAuthDAO = new SqlAuthDAO();
        } catch (ResultExceptions e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        //or create new WebSocketServer
        Spark.webSocket("/ws", new WebSocketServer());

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


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
