package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.requests.JoinGameRequest;
import service.ResultExceptions;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    public Service userService = new Service();

    public JoinGameHandler() throws ResultExceptions, DataAccessException {
    }

    public Object handle(Request request, Response response) throws DataAccessException {
        var joinGameInfo = new Gson().fromJson(request.body(), JoinGameInfo.class);
        var joinGameRequest = new JoinGameRequest(joinGameInfo.playerColor(), joinGameInfo.gameID(),
                request.headers("Authorization"));
        try {
            var joinGameResult = userService.joinGame(joinGameRequest);
            response.status(200);
            response.body(new Gson().toJson(joinGameResult));
            return response.body();
        } catch (ResultExceptions e) {
            response.status(500);
            response.body("{ \"message\": \"Error: unable to create game\" }");
            return response.body();
        } catch (ResultExceptions.AuthorizationError e) {
            response.status(401);
            response.body("{ \"message\": \"Error: unauthorized\" }");
            return response.body();
        } catch (ResultExceptions.BadRequestError e) {
            response.status(400);
            response.body("{ \"message\": \"Error: bad request\" }");
            return response.body();
        } catch (ResultExceptions.AlreadyTakenError e) {
            response.status(403);
            response.body("{ \"message\": \"Error: already taken\" }");
            return response.body();
        }
    }
}
