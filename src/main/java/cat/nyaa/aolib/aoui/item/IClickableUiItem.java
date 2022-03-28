package cat.nyaa.aolib.aoui.item;

import cat.nyaa.aolib.aoui.data.WindowClickData;
import org.bukkit.entity.Player;

public interface IClickableUiItem extends IUiItem {
    void onClick(WindowClickData clickData, Player player);
}
