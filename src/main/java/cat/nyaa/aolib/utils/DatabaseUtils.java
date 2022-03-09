package cat.nyaa.aolib.utils;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class DatabaseUtils {
    public static Optional<Connection> newSqliteJdbcConnection(@NotNull File file) {
        Connection result = null;
        try {
            result = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            result.setAutoCommit(true);
            return Optional.of(result);
        } catch (SQLException throwables) {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException ignored) {
                }
            }
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    public static Optional<Connection> newSqliteJdbcConnection(@NotNull File dataFolder, @NotNull String fileName) {
        File f = new File(dataFolder, fileName);
        return newSqliteJdbcConnection(f);
    }

    public static Optional<Connection> newSqliteJdbcConnection(@NotNull Plugin plugin, String fileName) {
        return newSqliteJdbcConnection(plugin.getDataFolder(), fileName);
    }

    public static Optional<Connection> newSqliteJdbcConnection(@NotNull Plugin plugin) {
        return newSqliteJdbcConnection(plugin, "SQLiteDatabase.db");
    }

    @Contract("_, _, _ -> new")
    public static @NotNull CompletableFuture<Integer> executeUpdateAsync(Connection connection, String sql, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return connection.createStatement().executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }, executor);
    }

    /**
     * @param connection jdbc connection
     * @param plugin     plugin
     * @param filename   sql file name
     * @param executor   executor
     * @return future
     */
    public static @NotNull CompletableFuture<Integer> executeUpdateAsync(Connection connection, Plugin plugin, String filename, Executor executor) {
        try (InputStream inputStream = plugin.getResource("sql/" + filename)) {
            if (inputStream == null) {
                return CompletableFuture.completedFuture(-1);
            }
            return executeUpdateAsync(connection, new String(inputStream.readAllBytes()), executor);
        } catch (IOException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(-1);
        }
    }

    public static <T> CompletableFuture<T> executeQueryAsync(Connection connection, Plugin plugin, String filename, Executor executor, Function<ResultSet, T> function) {
        try (InputStream inputStream = plugin.getResource("sql/" + filename)) {
            if (inputStream == null) {
                return CompletableFuture.completedFuture(null);
            }
            return executeQueryAsync(connection, new String(inputStream.readAllBytes()), executor, function);
        } catch (IOException e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(null);
        }
    }

    @Contract("_, _, _, _ -> new")
    public static <T> @NotNull CompletableFuture<T> executeQueryAsync(Connection connection, String sql, Executor executor, Function<ResultSet, T> function) {
        return CompletableFuture.supplyAsync(() -> {
            try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
                return function.apply(resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }, executor);
    }
}