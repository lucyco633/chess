package ui.repl;

import chess.ChessGame;
import server.ResponseException;
import ui.GameplayClient;

public class GameplayRepl {
    private final GameplayClient client;

    public GameplayRepl(String url, String authToken, int gameId,
                        ChessGame chessGame, String team) throws ResponseException {
        client = new GameplayClient(url, authToken, gameId, chessGame, team);
    }
}
