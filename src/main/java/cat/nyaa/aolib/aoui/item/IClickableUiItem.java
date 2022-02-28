package cat.nyaa.aolib.aoui.item;

import cat.nyaa.aolib.network.data.DataClickType;
import org.bukkit.entity.Player;

public interface IClickableUiItem extends IUiItem {
    void onClick(DataClickType clickType, Player player);

    default void onClick(int slotNum, int buttonNum, DataClickType clickType, Player player) {
        onClick(clickType, player);
    }
}
