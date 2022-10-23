package cat.nyaa.aolib.command.subcommand;

import cat.nyaa.aolib.AoLibPlugin;
import cat.nyaa.aolib.aoui.IBaseUI;
import cat.nyaa.aolib.aoui.PageUI;
import cat.nyaa.aolib.aoui.UIManager;
import cat.nyaa.aolib.aoui.item.CommandUiItem;
import cat.nyaa.aolib.aoui.item.IUiItem;
import cat.nyaa.aolib.npc.NpcManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AoDebugCommand implements org.bukkit.command.TabExecutor {
    private static NpcManager debug_npcManager;
    private static UIManager debug_uiManager;

    public static void debugEnable(AoLibPlugin plugin) {
        if (!AoLibPlugin.isDEBUG()) return;
        debug_npcManager = new NpcManager(plugin);
        debug_uiManager = new UIManager(plugin);
    }

    public static void debugDisable() {
        if (!AoLibPlugin.isDEBUG()) return;
        debug_npcManager.destructor();
        debug_uiManager.destructor();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!AoLibPlugin.isDEBUG()) return false;
        if (sender instanceof Player) {
            //debug_npcManager.sendAddNpc((Player) sender, new BasePlayerNpc((Player) sender));
            var itemList = new ArrayList<IUiItem>();
            for (int i = 0; i < 100; i++) {
                itemList.add(CommandUiItem.create(getDebugItem(i), null, "me " + i, null));
            }
            debug_uiManager.sendOpenWindow((Player) sender, new PageUI(itemList, (IBaseUI ui) -> debug_uiManager.broadcastFullState(ui), ""));
        }
        return false;
    }

    private ItemStack getDebugItem(int num) {
        var item = new ItemStack(Material.BAKED_POTATO);
        var meta = item.getItemMeta();
        if (meta == null) return item;
        meta.displayName(Component.text(num));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
