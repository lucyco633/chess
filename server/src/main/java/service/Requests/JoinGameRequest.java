package service.Requests;

public record JoinGameRequest(String playerColor, Integer gameID, String authToken) {
}
