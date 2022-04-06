package cat.nyaa.aolib.message;

import cat.nyaa.aolib.AoLibPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MessageListener implements Listener {
    private final AoLibPlugin plugin;

    public MessageListener(@NotNull AoLibPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        PlayerJoinTask.createAndRun(this.plugin, event.getPlayer().getUniqueId());
    }

    public static class PlayerJoinTask extends BukkitRunnable {
        private final UUID playerUUID;

        PlayerJoinTask(UUID playerUUID) {
            this.playerUUID = playerUUID;
        }

        public static @NotNull BukkitTask createAndRun(Plugin plugin, UUID playerUUID) {
            var task = new PlayerJoinTask(playerUUID);
            return task.runTaskLater(plugin, 200);
        }

        @Override
        public void run() {
            var player = Bukkit.getPlayer(playerUUID);
            if (AoMessage.getInstance() != null && player != null && player.isOnline()) {
                AoMessage.getInstance().AfterPlayerJoin(playerUUID);
            }
        }
    }
}
