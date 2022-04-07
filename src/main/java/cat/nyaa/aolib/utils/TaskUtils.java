package cat.nyaa.aolib.utils;

import cat.nyaa.aolib.AoLibPlugin;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class TaskUtils {
    public static class tickScheduler {
        public static int get5BitId(@NotNull UUID uniqueId) {//MAX= 0001 1111 =31
            return getBitId(uniqueId, 5);
        }

        public static int getBitId(@NotNull UUID uniqueId, int bit) {
            long l = (uniqueId.getMostSignificantBits() >> 1) + (uniqueId.getLeastSignificantBits() >> 1);
            return (int) ((l >>> (64 - bit)));
        }

        public static void waitToRun(long tickCount, int bit, @NotNull UUID uniqueId, @NotNull Runnable runnable) {
            int mod = 1 << bit;
            int hashedId = getBitId(uniqueId, bit);
            if (hashedId == (tickCount % mod)) {
                runnable.run();
            }
        }

        public static void mod128TickToRun(long tickCount, @NotNull UUID uniqueId, @NotNull Runnable runnable) {
            waitToRun(tickCount, 7, uniqueId, runnable);
        }

        public static void mod64TickToRun(long tickCount, @NotNull UUID uniqueId, @NotNull Runnable runnable) {
            waitToRun(tickCount, 6, uniqueId, runnable);
        }

        public static void mod32TickToRun(long tickCount, @NotNull UUID uniqueId, @NotNull Runnable runnable) {
            waitToRun(tickCount, 5, uniqueId, runnable);
        }

        public static void mod16TickToRun(long tickCount, @NotNull UUID uniqueId, @NotNull Runnable runnable) {
            waitToRun(tickCount, 4, uniqueId, runnable);
        }

        public static void mod8TickToRun(long tickCount, @NotNull UUID uniqueId, @NotNull Runnable runnable) {
            waitToRun(tickCount, 3, uniqueId, runnable);
        }

        public static void mod4TickToRun(long tickCount, @NotNull UUID uniqueId, @NotNull Runnable runnable) {
            waitToRun(tickCount, 2, uniqueId, runnable);
        }

        public static void mod2TickToRun(long tickCount, @NotNull UUID uniqueId, @NotNull Runnable runnable) {
            waitToRun(tickCount, 1, uniqueId, runnable);
        }
    }

    public static class async {
        public static Executor mainThreadExecutor = new MainThreadExecutor();

        public static void onTick() {
            while (!MainThreadExecutor.isEmpty()) {
                var runnable = MainThreadExecutor.poll();
                try {
                    runnable.run();
                } catch (Exception e) {
                    if (AoLibPlugin.instance != null) {
                        AoLibPlugin.instance.getLogger().warning("Exception in main thread executor");
                    }
                    e.printStackTrace();
                }
            }
        }

        public static <T> T getSyncDefault(@NotNull Supplier<@NotNull T> supplier, @Nullable T defaultValue) {
            var result = getSync(supplier);
            if (result.isEmpty()) {
                return defaultValue;
            }
            return result.get();
        }

        public static <T> Optional<T> getSync(@NotNull Supplier<T> supplier) {
            try {
                return Optional.ofNullable(getSyncThrow(supplier));
            } catch (CancellationException cancellationException) {
                if (AoLibPlugin.instance != null) {
                    AoLibPlugin.instance.getLogger().warning("Task cancelled");
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }

        public static <T> T getSyncThrow(@NotNull Supplier<T> supplier) throws ExecutionException, InterruptedException,
                CancellationException {
            var result = callSync(supplier);
            return result.get();
        }

        public static <T> CompletableFuture<T> callSync(@NotNull Supplier<T> supplier) {
            if (Bukkit.isPrimaryThread()) {
                return CompletableFuture.completedFuture(supplier.get());
            } else {
                return runSyncMethod(supplier);
            }
        }

        public static @NotNull CompletableFuture<Void> callSync(@NotNull Runnable runnable) {
            if (Bukkit.isPrimaryThread()) {
                runnable.run();
                return CompletableFuture.completedFuture(null);
            } else {
                return runSyncMethod(runnable);
            }

        }

        public static <T> @NotNull CompletableFuture<T> runSyncMethod(@NotNull Supplier<T> task) {
            return CompletableFuture.supplyAsync(task, mainThreadExecutor);
        }

        public static @NotNull CompletableFuture<Void> runSyncMethod(@NotNull Runnable task) {
            return CompletableFuture.runAsync(task, mainThreadExecutor);
        }

        private static class MainThreadExecutor implements Executor {
            public static ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

            public static boolean isEmpty() {
                return taskQueue.isEmpty();
            }

            public static Runnable poll() {
                return taskQueue.poll();
            }

            @Override
            public void execute(@NotNull Runnable command) {
                taskQueue.offer(command);
            }
        }


    }
}
