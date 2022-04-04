package cat.nyaa.aolib.message;

import cat.nyaa.aolib.AoLibPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

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
}
