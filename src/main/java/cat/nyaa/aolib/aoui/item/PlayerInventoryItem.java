package cat.nyaa.aolib.aoui.item;

import cat.nyaa.aolib.aoui.UIPlayerHold;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public record PlayerInventoryItem(int slot) implements IUiItem {
    @Override
    public ItemStack getWindowItem(Player player) {
        ItemStack itemStack = player.getInventory().getItem(slot);
        return Objects.isNull(itemStack) ? UIPlayerHold.EMPTY_ITEM.clone() : itemStack.clone();
    }
}
