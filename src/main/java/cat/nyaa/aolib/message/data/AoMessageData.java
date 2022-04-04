package cat.nyaa.aolib.message.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record AoMessageData(int msgId, String msg, MessageType msgType, UUID player, long createdAt) {
    @Contract("_ -> new")
    public static @NotNull AoMessageData fromResultSet(@NotNull ResultSet rs) throws SQLException {
        return new AoMessageData(
                rs.getInt("msg_id"),
                rs.getString("msg"),
                MessageType.valueOf(rs.getString("msg_type")),
                UUID.fromString(rs.getString("player")),
                rs.getLong("created_at")
        );
    }

    public enum MessageType {
        STRING_MESSAGE,
        JSON
    }
}
