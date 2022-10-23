package cat.nyaa.aolib.command.subcommand;

import cat.nyaa.aolib.AoLibPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AoReloadCommand implements org.bukkit.command.TabExecutor {
    private final AoLibPlugin plugin;

    public AoReloadCommand(AoLibPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("aolib.command.reload")) return false;
        plugin.onReload();
        AoLibPlugin.getI18n().ifPresent(I18n -> sender.sendMessage(I18n.getFormatted("command.reload.complete")));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
