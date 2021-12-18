package cat.nyaa.aolib;

import cat.nyaa.aolib.aoui.IBaseUI;
import cat.nyaa.aolib.aoui.UIPlayerHold;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface UISynchronizer {

    void sendInitialData(UIPlayerHold uiPlayerHold, List<ItemStack> items, ItemStack carriedItem, int[] data);

    void sendSlotChange(UIPlayerHold uiPlayerHold, int slot, ItemStack itemStack);

    void sendCarriedChange(UIPlayerHold uiPlayerHold, ItemStack itemStack);

    void sendDataChange(UIPlayerHold uiPlayerHold, int id, int value);
}