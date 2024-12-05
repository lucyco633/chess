package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

public class MakeMoveCommand extends UserGameCommand {

    ChessMove move;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
    }

    public ChessMove getChessMove() {
        return this.move;
    }
}
