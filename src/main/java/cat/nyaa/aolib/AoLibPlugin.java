package cat.nyaa.aolib;

import cat.nyaa.aolib.aoui.IBaseUI;
import cat.nyaa.aolib.aoui.PageUI;
import cat.nyaa.aolib.aoui.UIManager;
import cat.nyaa.aolib.aoui.item.CommandUiItem;
import cat.nyaa.aolib.aoui.item.IUiItem;
import cat.nyaa.aolib.message.AoMessage;
import cat.nyaa.aolib.npc.NpcManager;
import cat.nyaa.aolib.utils.EntityDataUtils;
import cat.nyaa.aolib.utils.TaskUtils;
import cat.nyaa.nyaacore.LanguageRepository;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;

public final class AoLibPlugin extends JavaPlugin {
    private static final boolean DEBUG = false;
    @Nullable
    public static AoLibPlugin instance = null;
    private static AolibI18n I18n = null;
    private NpcManager debug_npcManager;
    private UIManager debug_uiManager;
    private AoMessage AoMsg;
    private AolibTaskManager taskManager;

    public AoLibPlugin() {
    }

    private AoLibPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public static Optional<AolibI18n> getI18n() {
        return Optional.ofNullable(I18n);
    }

    @NotNull
    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    @Override
    public void onLoad() {
        instance = this;
        EntityDataUtils.init();
        saveDefaultConfig();
        reloadConfig();
        I18n = new AolibI18n(this, this.getConfig().getString("language", LanguageRepository.DEFAULT_LANGUAGE));
    }

    @Override
    public void onEnable() {
        this.AoMsg = new AoMessage(this);
        this.taskManager = new AolibTaskManager(this);
        if (DEBUG) this.debugEnable();
    }

    private void debugEnable() {
        this.debug_npcManager = new NpcManager(this);
        this.debug_uiManager = new UIManager(this);
    }

    private void onReload() {
        this.reloadConfig();
    }

    @Override
    public void onDisable() {
        if (AoMsg != null) {
            AoMsg.destructor();
            AoMsg = null;
        }
        if (taskManager != null) {
            taskManager.destructor();
            taskManager = null;
        }
        TaskUtils.async.onTick();

        if (DEBUG) this.debugDisable();
    }

    private void debugDisable() {
        this.debug_npcManager.destructor();
        this.debug_uiManager.destructor();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (DEBUG) {
            this.debugCommand(sender, command, label, args);
        }
        if (args.length >= 1) switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload":
                if (!sender.hasPermission("aolib.command.reload")) return false;
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
            //debug_npcManager.sendAddNpc((Player) sender, new BasePlayerNpc((Player) sender));
            var itemList = new ArrayList<IUiItem>();
            for (int i = 0; i < 100; i++) {
                itemList.add(CommandUiItem.create(getDebugItem(i), null, "me " + i, null));
            }
            debug_uiManager.sendOpenWindow((Player) sender, new PageUI(itemList, (IBaseUI ui) -> debug_uiManager.broadcastFullState(ui), ""));
        }
    }

    private ItemStack getDebugItem(int num) {
        var item = new ItemStack(Material.BAKED_POTATO);
        var meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(String.valueOf(num));
        item.setItemMeta(meta);
        return item;
    }
}
