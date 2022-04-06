package cat.nyaa.aolib.utils;

import cat.nyaa.aolib.AoLibPlugin;
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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


public class DatabaseUtils {
    @Contract("_ -> new")
    public static @NotNull CompletableFuture<Optional<Connection>> newSqliteJdbcConnection(@NotNull File file) {
        return CompletableFuture.supplyAsync(() -> {
            Connection result = null;
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException ignored) {
            }
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
        });
    }

    public static @NotNull CompletableFuture<Optional<Connection>> newSqliteJdbcConnection(@NotNull File dataFolder, @NotNull String fileName) {
        File f = new File(dataFolder, fileName);
        return newSqliteJdbcConnection(f);
    }

    public static @NotNull CompletableFuture<Optional<Connection>> newSqliteJdbcConnection(@NotNull Plugin plugin, String fileName) {
        return newSqliteJdbcConnection(plugin.getDataFolder(), fileName);
    }

    public static @NotNull CompletableFuture<Optional<Connection>> newSqliteJdbcConnection(@NotNull Plugin plugin) {
        return newSqliteJdbcConnection(plugin, "SQLiteDatabase.db");
    }

    @Nullable
    private static PreparedStatement buildStatement(@NotNull Connection conn, @NotNull Supplier<String> sql, @Nullable Integer autoGeneratedKeys, Object... parameters) {
        String sqlString;
        try {
            sqlString = sql.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (sqlString == null || sqlString.isEmpty()) {
            if (AoLibPlugin.instance != null) {
                AoLibPlugin.instance.getLogger().warning("buildStatement error:sql is empty");
            }
            return null;
        }

        PreparedStatement stmt = null;
        try {
            if (autoGeneratedKeys != null) {
                stmt = conn.prepareStatement(sqlString, autoGeneratedKeys);
            } else {
                stmt = conn.prepareStatement(sqlString);
            }
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
    private static PreparedStatement buildStatement(@NotNull Connection conn, @Nullable Plugin plugin, @NotNull String filename, @Nullable Integer autoGeneratedKeys, Object... parameters) {
        return buildStatement(conn, () -> {
            InputStream inputStream;
            if (plugin == null) {
                inputStream = DatabaseUtils.class.getClassLoader().getResourceAsStream("sql/" + filename);
            } else {
                inputStream = plugin.getResource("sql/" + filename);
            }
            if (inputStream == null) {
                if (AoLibPlugin.instance != null) {
                    if (plugin != null) {
                        AoLibPlugin.instance.getLogger().warning("buildStatement error: can not load sql file: " + filename + " from " + plugin.getName());
                    } else {
                        AoLibPlugin.instance.getLogger().warning("buildStatement error: can not load sql file: " + filename);
                    }
                }
                return null;
            }
            try (inputStream) {
                return new String(inputStream.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }, autoGeneratedKeys, parameters);


    }

    /**
     * @param connection jdbc connection
     * @param plugin     plugin
     * @param filename   sql file name
     * @param executor   executor
     * @param parameters parameters
     * @return future
     */
    public static @NotNull CompletableFuture<Optional<Integer>> executeUpdateAsync(Connection connection, Plugin plugin, String filename, Executor executor, Object... parameters) {
        return executeUpdateAsync(() -> buildStatement(connection, plugin, filename, null, parameters), executor);
    }

    public static @NotNull CompletableFuture<Optional<Integer>> executeUpdateAsync(Connection connection, String sql, Executor executor, Object... parameters) {
        return executeUpdateAsync(() -> buildStatement(connection, () -> sql, null, parameters), executor);
    }

    public static @NotNull CompletableFuture<Optional<Integer>> executeUpdateAsync(Supplier<PreparedStatement> stmtSupplier, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            var stmt = stmtSupplier.get();
            if (stmt == null) return Optional.empty();
            try {
                return Optional.of(stmt.executeUpdate());
            } catch (SQLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }, executor);
    }

    public static @NotNull <T> CompletableFuture<Optional<T>> executeUpdateAsyncAndGetAutoGeneratedKeys(Connection connection, Plugin plugin, String filename, Executor executor, BiFunction<Integer, ResultSet, T> biFunction, Object... parameters) {
        return executeUpdateAsyncAndGetAutoGeneratedKeys(() -> buildStatement(connection, plugin, filename, Statement.RETURN_GENERATED_KEYS, parameters), executor, biFunction);
    }

    public static @NotNull <T> CompletableFuture<Optional<T>> executeUpdateAsyncAndGetAutoGeneratedKeys(Connection connection, String sql, Executor executor, BiFunction<Integer, ResultSet, T> biFunction, Object... parameters) {
        return executeUpdateAsyncAndGetAutoGeneratedKeys(() -> buildStatement(connection, () -> sql, Statement.RETURN_GENERATED_KEYS, parameters), executor, biFunction);
    }

    public static @NotNull <T> CompletableFuture<Optional<T>> executeUpdateAsyncAndGetAutoGeneratedKeys(Supplier<PreparedStatement> stmtSupplier, Executor executor, BiFunction<Integer, ResultSet, T> biFunction) {
        return CompletableFuture.supplyAsync(() -> {
            var stmt = stmtSupplier.get();
            if (stmt == null) return Optional.empty();
            try {
                var rowCount = stmt.executeUpdate();
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    return Optional.ofNullable(biFunction.apply(rowCount, generatedKeys));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }, executor);
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static <T> @NotNull CompletableFuture<Optional<T>> executeQueryAsync(Connection connection, Plugin plugin, String filename, Executor executor, Function<ResultSet, T> function, Object... parameters) {
        return executeQueryAsync(() -> buildStatement(connection, plugin, filename, null, parameters), executor, function);
    }

    public static <T> @NotNull CompletableFuture<Optional<T>> executeQueryAsync(Connection connection, String sql, Executor executor, Function<ResultSet, T> function, Object... parameters) {
        return executeQueryAsync(() -> buildStatement(connection, () -> sql, null, parameters), executor, function);
    }

    @Contract("_, _, _ -> new")
    private static <T> @NotNull CompletableFuture<Optional<T>> executeQueryAsync(Supplier<PreparedStatement> stmtSupplier, Executor executor, Function<ResultSet, T> function) {
        return CompletableFuture.supplyAsync(() -> {
            var stmt = stmtSupplier.get();
            if (stmt == null) return Optional.empty();
            try (ResultSet resultSet = stmt.executeQuery()) {
                return Optional.ofNullable(function.apply(resultSet));
            } catch (SQLException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }, executor);
    }

}