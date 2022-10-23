package cat.nyaa.aolib;

import cat.nyaa.aolib.command.AolibCommand;
import cat.nyaa.aolib.command.subcommand.AoDebugCommand;
import cat.nyaa.aolib.i18n.AoI18n;
import cat.nyaa.aolib.message.AoMessage;
import cat.nyaa.aolib.utils.TaskUtils;
import cat.nyaa.nyaacore.LanguageRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public final class AoLibPlugin extends JavaPlugin {
    private static final boolean DEBUG = false;
    @Nullable
    public static AoLibPlugin instance = null;
    private static AolibI18n I18n = null;
    private boolean isTest = false;
    private AoMessage AoMsg;
    private AolibTaskManager taskManager;
    private AolibCommand command;

    public AoLibPlugin() {
    }

    private AoLibPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file, boolean test) {
        super(loader, description, dataFolder, file);
        this.isTest = test;
    }

    public static boolean isDEBUG() {
        return DEBUG;
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
        saveDefaultConfig();
        reloadConfig();
        I18n = new AolibI18n(this, this.getConfig().getString("language", LanguageRepository.DEFAULT_LANGUAGE));
        AoI18n.load(this);
    }

    @Override
    public void onEnable() {
        this.AoMsg = new AoMessage(this);
        this.taskManager = new AolibTaskManager(this);
        this.command = new AolibCommand(this);
        Objects.requireNonNull(this.getCommand("aolib")).setExecutor(command);
        Objects.requireNonNull(this.getCommand("aolib")).setTabCompleter(command);
        if (isDEBUG()) AoDebugCommand.debugEnable(this);
    }


    public void onReload() {
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

        if (isDEBUG()) AoDebugCommand.debugDisable();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length >= 1) if ("reload".equals(args[0].toLowerCase(Locale.ROOT))) {
            if (!sender.hasPermission("aolib.command.reload")) return false;
            this.onReload();
            getI18n().ifPresent(I18n -> sender.sendMessage(I18n.getFormatted("command.reload.complete")));
            return true;
        } else {
            return false;
        }
        return true;
    }


}
