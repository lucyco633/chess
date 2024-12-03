package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

public class MakeMoveCommand {
    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;

    private final ChessMove chessMove;


    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove chessMove) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.chessMove = chessMove;
    }

    public enum CommandType {
        MAKE_MOVE
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    public ChessMove getChessMove() {
        return chessMove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MakeMoveCommand)) {
            return false;
        }
        MakeMoveCommand that = (MakeMoveCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID(), getChessMove());
    }
}
