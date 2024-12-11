package websocket.messages;

import java.util.Objects;

public class LoadGameMessage extends ServerMessage {

    public String game;

    public LoadGameMessage(ServerMessageType type, String game) {
        super(type);
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        LoadGameMessage that = (LoadGameMessage) o;
        return Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), game);
    }

    public String getGame() {
        return game;
    }
}
