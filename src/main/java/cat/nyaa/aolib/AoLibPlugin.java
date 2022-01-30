package cat.nyaa.aolib;

import cat.nyaa.aolib.aoui.BaseUI;
import cat.nyaa.aolib.aoui.UIManager;
import cat.nyaa.aolib.utils.EntityDataUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class AoLibPlugin extends JavaPlugin {

    private UIManager uiManager;

    @Override
    public void onEnable() {
        EntityDataUtils.init();
        this.uiManager = new UIManager(this);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        this.uiManager.destructor();
        this.uiManager = null;

        // Plugin shutdown logic
    }

    public UIManager getUiManager() {
        return uiManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (uiManager == null) return false;
        if (!(sender instanceof Player)) return false;
        uiManager.sendOpenWindow((Player) sender, new BaseUI());
        return true;
    }
}
