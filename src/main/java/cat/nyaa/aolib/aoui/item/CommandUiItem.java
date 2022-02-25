package cat.nyaa.aolib.aoui.item;

import cat.nyaa.aolib.aoui.UIManager;
import cat.nyaa.aolib.network.data.DataClickType;
import cat.nyaa.aolib.utils.RunCommandUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

public class CommandUiItem implements IClickableUiItem, IUiItem {
    private final String commandPermission;
    private final String command;
    private final Player player;
    private final Plugin plugin;
    private final UIManager uiManager;
    private final ItemStack holdItem;

    public CommandUiItem(Plugin plugin, ItemStack holdItem, @Nullable UIManager uiManager, Player player, String command, @Nullable String commandPermission) {
        this.plugin = plugin;
        this.holdItem = holdItem;
        this.uiManager = uiManager;
        this.player = player;
        this.command = command;
        this.commandPermission = commandPermission;
    }

    @Override
    public void onClick(DataClickType clickType, Player player) {
        if (clickType.equals(DataClickType.PICKUP)) {
            RunCommandUtils.executeCommand(player, command, commandPermission, plugin);
            if (uiManager != null) {
                uiManager.sendCloseWindow(player);
            }
        }
    }

    @Override
    public ItemStack getWindowItem(Player player) {
        return holdItem;
    }
}
