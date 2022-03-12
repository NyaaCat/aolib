package cat.nyaa.aolib.utils;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;


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

    @Nullable
    private static PreparedStatement buildStatement(@NotNull Connection conn, @NotNull Supplier<String> sql, Object... parameters) {
        String sqlString;
        try {
            sqlString = sql.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (sqlString == null || sqlString.isEmpty()) return null;

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sqlString);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    Object javaObj = parameters[i];
                    stmt.setObject(i + 1, javaObj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException ignored) {
            }
            return null;
        }
        return stmt;
    }

    @Nullable
    private static PreparedStatement buildStatement(@NotNull Connection conn, @Nullable Plugin plugin, @NotNull String filename, Object... parameters) {
        return buildStatement(conn, () -> {
            InputStream inputStream;
            if (plugin == null) {
                inputStream = DatabaseUtils.class.getClassLoader().getResourceAsStream("sql/" + filename);
            } else {
                inputStream = plugin.getResource("sql/" + filename);
            }
            try (inputStream) {
                if (inputStream == null) {
                    return null;
                }
                return new String(inputStream.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }, parameters);


    }
    /**
     * @param connection jdbc connection
     * @param plugin     plugin
     * @param filename   sql file name
     * @param executor   executor
     * @param parameters parameters
     * @return future
     */
    public static @NotNull CompletableFuture<Integer> executeUpdateAsync(Connection connection, Plugin plugin, String filename, Executor executor, Object... parameters) {
        return executeUpdateAsync(() -> buildStatement(connection, plugin, filename, parameters), executor);
    }

    public static @NotNull CompletableFuture<Integer> executeUpdateAsync(Connection connection, String sql, Executor executor, Object... parameters) {
        return executeUpdateAsync(() -> buildStatement(connection, () -> sql, parameters), executor);
    }

    public static @NotNull CompletableFuture<Integer> executeUpdateAsync(Supplier<PreparedStatement> stmtSupplier, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            var stmt = stmtSupplier.get();
            if (stmt == null) return -1;
            try {
                return stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }, executor);
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static <T> @NotNull CompletableFuture<T> executeQueryAsync(Connection connection, Plugin plugin, String filename, Executor executor, Function<ResultSet, T> function, Object... parameters) {
        return executeQueryAsync(() -> buildStatement(connection, plugin, filename, parameters), executor, function);
    }

    public static <T> @NotNull CompletableFuture<T> executeQueryAsync(Connection connection, String sql, Executor executor, Function<ResultSet, T> function, Object... parameters) {
        return executeQueryAsync(() -> buildStatement(connection, () -> sql, parameters), executor, function);
    }

    @Contract("_, _, _ -> new")
    private static <T> @NotNull CompletableFuture<T> executeQueryAsync(Supplier<PreparedStatement> stmtSupplier, Executor executor, Function<ResultSet, T> function) {
        return CompletableFuture.supplyAsync(() -> {
            var stmt = stmtSupplier.get();
            if (stmt == null) return null;
            try (ResultSet resultSet = stmt.executeQuery()) {
                return function.apply(resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }, executor);
    }
}