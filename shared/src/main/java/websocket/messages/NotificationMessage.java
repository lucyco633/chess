package websocket.messages;

import java.util.Objects;

public class NotificationMessage extends ServerMessage {

    String message;

    public NotificationMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NotificationMessage that = (NotificationMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), message);
    }
}