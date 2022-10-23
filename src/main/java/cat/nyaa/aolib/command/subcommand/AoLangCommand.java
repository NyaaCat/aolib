package cat.nyaa.aolib.command.subcommand;

import cat.nyaa.aolib.AoLibPlugin;
import cat.nyaa.aolib.i18n.AoI18n;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AoLangCommand implements TabExecutor {
    private static final List<String> LANG_SUBCOMMANDS = ImmutableList.of("info", "sort");
    private String usageMessage;

    public AoLangCommand(AoLibPlugin plugin) {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }
        switch (args[0]) {
            case "info" -> sender.sendMessage(getInfo());
            case "sort" -> sender.sendMessage(doSort());
        }
        return true;
    }

    private String doSort() {
        return AoI18n.sort();
    }

    private @NotNull String getInfo() {
        return AoI18n.treeInfo();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], LANG_SUBCOMMANDS,
                    new ArrayList<>(LANG_SUBCOMMANDS.size()));
        }
        return ImmutableList.of();
    }
}
