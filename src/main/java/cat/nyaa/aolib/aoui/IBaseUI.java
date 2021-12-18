package cat.nyaa.aolib.aoui;

import cat.nyaa.aolib.network.data.DataClickType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IBaseUI {

    void onWindowClose();

    int getWindowId();

    int getTypeId();

    @NotNull
    BaseComponent getTitle();

    @NotNull
    List<ItemStack> getWindowItem();

    @NotNull
    ItemStack getCarriedWindowItem();

    int[] getWindowData();

    void onButtonClick(int buttonId);

    void onWindowClick(int slotNum, int buttonNum, DataClickType clickType, Player player);


}
