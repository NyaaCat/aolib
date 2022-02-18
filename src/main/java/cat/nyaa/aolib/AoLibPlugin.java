package cat.nyaa.aolib;

import cat.nyaa.aolib.npc.BasePlayerNpc;
import cat.nyaa.aolib.npc.NpcManager;
import cat.nyaa.aolib.utils.EntityDataUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

public final class AoLibPlugin extends JavaPlugin {
    private static final boolean DEBUG = true;//todo false
    private NpcManager debug_npcManager;
    public static AoLibPlugin instance = null;
    private static AolibI18n I18n = null;

    @Override
    public void onLoad() {
        instance = this;
        EntityDataUtils.init();
        I18n = new AolibI18n(this, this.getConfig().getString("language", "en_US"));
    }

    @Override
    public void onEnable() {
        if (DEBUG) this.debugEnable();
    }

    private void debugEnable() {
        this.debug_npcManager = new NpcManager();
    }

    private void onReload() {
        this.reloadConfig();
    }

    @Override
    public void onDisable() {
        if (DEBUG) this.debugDisable();
    }

    private void debugDisable() {
        this.debug_npcManager.destructor();
    }

    public static Optional<AolibI18n> getI18n() {
        return Optional.ofNullable(I18n);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (DEBUG) {
            this.debugCommand(sender, command, label, args);
        }
        if (args.length >= 1) switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload":
                this.onReload();
                getI18n().ifPresent(I18n -> sender.sendMessage(I18n.getFormatted("command.reload.complete")));
                return true;
            default:
                return false;
        }
        return true;
    }


    private void debugCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            debug_npcManager.sendAddNpc((Player) sender, new BasePlayerNpc((Player) sender));
        }
    }
}
