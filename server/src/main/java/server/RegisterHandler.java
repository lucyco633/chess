package server;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.Requests.RegisterRequest;
import service.ResultExceptions;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route{
    public UserService userService = new UserService();

    public Object handle(Request request, Response response) throws DataAccessException, ResultExceptions {
        var registerRequest = new Gson().fromJson(request.body(), RegisterRequest.class);
        try {
            var registerResult = userService.register(registerRequest);
            response.status(200);
            //how to put result into body??
            response.body(new Gson().toJson(registerResult));
            return response.body();
        }
        catch (ResultExceptions e){
            response.status(500);
            response.body("{ \"message\": \"Error: unable to create game\" }");
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
