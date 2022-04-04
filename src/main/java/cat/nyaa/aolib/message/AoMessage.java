package cat.nyaa.aolib.message;

import cat.nyaa.aolib.AoLibPlugin;
import cat.nyaa.aolib.message.data.AoMessageData;
import cat.nyaa.aolib.utils.DBFunctionUtils;
import cat.nyaa.aolib.utils.DatabaseUtils;
import cat.nyaa.aolib.utils.TaskUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Function;

import static cat.nyaa.aolib.message.data.AoMessageData.MessageType.JSON;
import static cat.nyaa.aolib.message.data.AoMessageData.MessageType.STRING_MESSAGE;

public class AoMessage {
    private static final LinkedBlockingQueue<Runnable> databaseExecutorQueue = new LinkedBlockingQueue<>();
    public static final ExecutorService databaseExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, databaseExecutorQueue);
    @Nullable
    private static AoMessage instance;
    private final AoLibPlugin plugin;
    private final SimpleDateFormat simpleDateFormat;
    private final MessageListener listener;
    private Connection jdbcConnection;

    public AoMessage(AoLibPlugin plugin) {
        this.plugin = plugin;
        initDB();
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd DD HH:mm:ss.SSS (z)");
        instance = this;
        this.listener = new MessageListener(plugin);
    }

    public static @Nullable AoMessage getInstance() {
        return instance;
    }

    public void destructor() {
        if (this.jdbcConnection != null) {
            try {
                this.jdbcConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        HandlerList.unregisterAll(listener);
    }

    private void sendMessageTo(UUID playerId, String... messages) {
        var player = Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline()) {
            for (String message : messages) {
                this.newOfflineMessage(playerId, STRING_MESSAGE, message);
            }
        } else {
            player.sendMessage(messages);
        }
    }

    private void sendMessageTo(UUID playerId, BaseComponent... messages) {
        var player = Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline()) {
            this.newOfflineMessage(playerId, JSON, ComponentSerializer.toString(messages));
        } else {
            player.spigot().sendMessage(messages);
        }
    }

    private CompletableFuture<Boolean> newOfflineMessage(UUID playerId, AoMessageData.MessageType messageType, String messageStr) {
        var result = getConnection(
                (conn) ->
                        DatabaseUtils.executeUpdateAsync(
                                        conn,
                                        plugin,
                                        "aomsg/new_offline_message.sql",
                                        databaseExecutor,
                                        messageStr,
                                        messageType.toString(),
                                        playerId.toString(),
                                        System.currentTimeMillis()
                                )
                                .thenApply(optInt -> optInt.isPresent() && optInt.get() > 0)
        );
        if (result.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        } else {
            return result.get();
        }
    }

    private CompletableFuture<List<AoMessageData>> getPlayerOfflineMessageList(UUID playerId) {
        var result = getConnection(
                (conn) ->
                        DatabaseUtils.executeQueryAsync(
                                        conn,
                                        plugin,
                                        "aomsg/get_player_message_data.sql",
                                        databaseExecutor,
                                        DBFunctionUtils.getDataListFromResultSet(AoMessageData.class),
                                        playerId.toString()
                                )
                                .thenApply(optList -> optList.orElse(List.of()))
        );
        if (result.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        } else {
            return result.get();
        }
    }

    private CompletableFuture<Optional<int[]>> deleteOfflineMessage(int... id) {
        return deleteOfflineMessage(Ints.asList(id));
    }

    private CompletableFuture<Optional<int[]>> deleteOfflineMessage(List<Integer> id) {
        Optional<CompletableFuture<Optional<int[]>>> result = getConnection((conn) -> CompletableFuture.supplyAsync(() -> {
            try (var ps = conn.prepareStatement("DELETE FROM ao_msg WHERE msg_id=?;")) {
                for (int i : id) {
                    ps.setInt(1, i);
                    ps.addBatch();
                }
                return Optional.of(ps.executeBatch());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }, databaseExecutor));
        if (result.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        } else {
            return result.get();
        }

    }

    private boolean sendMessageData(AoMessageData messageData) {//async
        return TaskUtils.async.callSyncAndGet(() -> {
            Player player = Bukkit.getPlayer(messageData.player());
            if (player == null || !player.isOnline()) return false;
            var pre = TextComponent.fromLegacyText("[" + simpleDateFormat.format(new Date(messageData.createdAt())) + "]");
            BaseComponent[] message = new BaseComponent[0];
            switch (messageData.msgType()) {
                case STRING_MESSAGE -> message = TextComponent.fromLegacyText(messageData.msg());
                case JSON -> message = ComponentSerializer.parse(messageData.msg());
                default -> {
                    var optI18n = AoLibPlugin.getI18n();
                    if (optI18n.isPresent()) {
                        message = TextComponent.fromLegacyText(optI18n.get().getFormatted("message.unknown_message_type", messageData.msgType().toString()));
                    }
                }
            }
            player.spigot().sendMessage(ObjectArrays.concat(pre, message, BaseComponent.class));
            return true;
        });
    }

    public void initDB() {
        getConnection(conn -> DatabaseUtils.executeUpdateAsync(conn, plugin, "aomsg/init.sql", databaseExecutor));
    }

    private <T> Optional<T> getConnection(Function<Connection, T> function) {
        var optConn = getJdbcConnection();
        if (optConn.isEmpty()) return Optional.empty();
        return optConn.map(function);
    }

    private Optional<Connection> getJdbcConnection() {
        try {
            if (this.jdbcConnection != null && !this.jdbcConnection.isClosed()) return Optional.of(this.jdbcConnection);
        } catch (SQLException e) {
            if (jdbcConnection != null) {
                try {
                    jdbcConnection.close();
                } catch (SQLException ignored) {
                }
            }
            e.printStackTrace();
        }
        try {
            var conn = DatabaseUtils.newSqliteJdbcConnection(plugin, "ao_message.db").get();
            if (conn.isEmpty()) {
                plugin.getLogger().warning("[AO MSG]Failed to create jdbc connection");
            } else {
                this.jdbcConnection = conn.get();
                return Optional.of(this.jdbcConnection);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    void AfterPlayerJoin(UUID playerId) {
        getPlayerOfflineMessageList(playerId)
                .thenAcceptAsync(list -> list.forEach(msgData -> {
                            List<Integer> MsgIds = Lists.newArrayList();
                            if (sendMessageData(msgData)) {
                                MsgIds.add(msgData.msgId());
                            }
                            try {
                                deleteOfflineMessage(MsgIds).get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                ));
    }


}
