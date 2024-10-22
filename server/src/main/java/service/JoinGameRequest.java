package service;

public record JoinGameRequest(String playerColor, Integer gameID, String authToken) {
}
