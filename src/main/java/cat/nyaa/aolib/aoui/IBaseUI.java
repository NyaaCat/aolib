package cat.nyaa.aolib.aoui;

import cat.nyaa.aolib.aoui.data.WindowClickData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IBaseUI {
    //after openWindow
    default void onWindowOpen(@NotNull Player player) {
    }

    //before remove
    default void onWindowClose() {
    }

    default void onButtonClick(int buttonId, Player player) {
    }

    int getWindowId();//async

    int getTypeId();

    @NotNull
    Component getTitle(Player player);

    @NotNull
    List<ItemStack> getWindowItem(Player player);

    @NotNull
    ItemStack getCarriedWindowItem(Player player);

    int getSlotSize();

    int[] getWindowData(Player player);

    int getDataSize();

    default void onWindowClick(WindowClickData windowClickData, Player player) {
    }
}
