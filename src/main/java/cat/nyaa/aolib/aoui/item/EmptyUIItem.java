package cat.nyaa.aolib.aoui.item;

import cat.nyaa.aolib.aoui.UIPlayerHold;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record EmptyUIItem() implements IUiItem {
    public static final EmptyUIItem EMPTY_UI_ITEM = new EmptyUIItem();

    @Override
    public ItemStack getWindowItem(Player player) {
        return UIPlayerHold.EMPTY_ITEM.clone();
    }
}
