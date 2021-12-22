package cat.nyaa.aolib.aoui.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IUiItem {
    ItemStack getWindowItem(Player player);
}
