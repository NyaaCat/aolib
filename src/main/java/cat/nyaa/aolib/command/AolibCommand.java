package cat.nyaa.aolib.command;

import cat.nyaa.aolib.AoLibPlugin;
import cat.nyaa.aolib.command.subcommand.AoDebugCommand;
import cat.nyaa.aolib.command.subcommand.AoLangCommand;
import cat.nyaa.aolib.command.subcommand.AoReloadCommand;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AolibCommand implements TabExecutor {
    final Map<String, TabExecutor> subCommands = new HashMap<>();

    public AolibCommand(AoLibPlugin plugin) {
        if (AoLibPlugin.isDEBUG()) {
            this.subCommands.put("debug", new AoDebugCommand());
        }
        this.subCommands.put("reload", new AoReloadCommand(plugin));
        this.subCommands.put("lang", new AoLangCommand(plugin));
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        } else {
            return subCommands.get(args[0]).onCommand(sender, command, args[0], Arrays.copyOfRange(args, 1, args.length));
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return ImmutableList.of();
        } else if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], subCommands.keySet(),
                    new ArrayList<>(subCommands.keySet().size()));
        } else {
            return subCommands.get(args[0]).onTabComplete(sender, command, args[0], Arrays.copyOfRange(args, 1, args.length));
        }
    }
}
