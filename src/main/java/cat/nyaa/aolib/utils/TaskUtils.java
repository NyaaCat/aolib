package cat.nyaa.aolib.utils;

import cat.nyaa.aolib.AoLibPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.Callable;

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
        public static <T> T callSyncAndGet(@NotNull Callable<T> callable) {
            return callSyncAndGet(callable, null);
        }

        @Nullable
        public static <T> T callSyncAndGet(@NotNull Callable<T> callable, @Nullable Plugin plugin) {
            if (Bukkit.isPrimaryThread()) {
                try {
                    return callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                var p = plugin;
                if (p == null) p = AoLibPlugin.instance;
                if (p == null) return null;
                try {
                    return Bukkit.getScheduler().callSyncMethod(p, callable).get();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }
}
