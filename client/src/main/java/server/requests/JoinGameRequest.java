package server.requests;

public record JoinGameRequest(String playerColor, Integer gameID, String authToken) {
}
