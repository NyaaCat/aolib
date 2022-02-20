package cat.nyaa.aolib.aoui.item;

import cat.nyaa.aolib.aoui.IBaseUI;
import cat.nyaa.aolib.aoui.UIManager;
import cat.nyaa.aolib.network.data.DataClickType;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public interface IClickableUiItem extends IUiItem {
    void onClick(DataClickType clickType, Player player);

    default void onClick(int slotNum, int buttonNum, DataClickType clickType, Player player) {
        onClick(clickType, player);
    }
}
