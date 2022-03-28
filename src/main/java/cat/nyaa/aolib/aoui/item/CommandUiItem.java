package cat.nyaa.aolib.aoui.item;

import cat.nyaa.aolib.AoLibPlugin;
import cat.nyaa.aolib.aoui.UIManager;
import cat.nyaa.aolib.aoui.data.WindowClickData;
import cat.nyaa.aolib.network.data.DataClickType;
import cat.nyaa.aolib.utils.CommandUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class CommandUiItem implements IClickableUiItem, IUiItem {
    @Nullable
    private final String commandPermission;
    @NotNull
    private final String command;
    @Nullable
    private final Player sender;
    @NotNull
    private final Plugin plugin;
    @Nullable
    private final UIManager uiManager;
    @NotNull
    private final ItemStack holdItem;

    public CommandUiItem(@NotNull Plugin plugin, @NotNull ItemStack holdItem, @Nullable UIManager uiManager, @Nullable Player sender, @NotNull String command, @Nullable String commandPermission) {
        this.plugin = plugin;
        this.holdItem = holdItem;
        this.uiManager = uiManager;
        this.sender = sender;
        this.command = command;
        this.commandPermission = commandPermission;
    }

    @Nullable
    public static CommandUiItem create(ItemStack holdItem, @Nullable UIManager uiManager, @NotNull String command, @Nullable String commandPermission) {
        if (AoLibPlugin.instance == null) return null;
        return new CommandUiItem(AoLibPlugin.instance, holdItem, uiManager, null, command, commandPermission);
    }

    @Override
    public ItemStack getWindowItem(Player player) {
        return holdItem;
    }

    @Override
    public void onClick(@NotNull WindowClickData clickData, Player player) {
        if (clickData.clickType().equals(DataClickType.PICKUP) && clickData.carriedItem().equals(holdItem)) {
            if (command.isEmpty()) return;
            CommandUtils.runCommand.executeCommand(sender == null ? player : sender, command, commandPermission, plugin);
            if (uiManager != null) {
                uiManager.sendCloseWindow(player);
            }
        }
    }
}
