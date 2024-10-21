package server;

import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
import dataaccess.DataAccessException;
import service.UserService;


public class ClearHandler implements HttpHandler {
    private final UserService service;

    public ClearHandler(UserService service) {
        this.service = service;
    }
    public void handle(HttpExchange exchange) throws IOException{

        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("delete")){
                Headers requestHeaders = exchange.getRequestHeaders();
                if (requestHeaders.isEmpty()){
                    service.clear();
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    success = true;
                }
            }
            if (!success){
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
                String resp = "{ \"message\": \"Error: unable to clear games\"";
                OutputStream respBody = exchange.getResponseBody();
                writeOutput(resp, respBody);
                exchange.getResponseBody().close();
            }
        } catch (DataAccessException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            String resp = "{ \"message\": \"Error: data access error\"";
            OutputStream respBody = exchange.getResponseBody();
            writeOutput(resp, respBody);
            exchange.getResponseBody().close();
        }
        catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            e.printStackTrace();
        }
    }
    private void writeOutput(String outString, OutputStream os) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(os);
        writer.write(outString);
        writer.flush();
    }
}
