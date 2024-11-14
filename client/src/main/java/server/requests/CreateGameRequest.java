package server.requests;

public record CreateGameRequest(String gameName, String authToken) {
}
