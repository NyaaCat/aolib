package cat.nyaa.aolib.aoui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class UIListener implements Listener {
    private final Plugin plugin;
    private final UIManager uiManager;

    public UIListener(Plugin plugin, UIManager uiManager) {
        this.plugin = plugin;
        this.uiManager = uiManager;
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        uiManager.handlePlayerQuit(event.getPlayer());

    }
}
