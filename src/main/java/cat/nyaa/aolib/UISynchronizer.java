package cat.nyaa.aolib;

import cat.nyaa.aolib.aoui.UIPlayerHold;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface UISynchronizer {

    void sendInitialData(@NotNull UIPlayerHold uiPlayerHold, @NotNull List<ItemStack> items, @NotNull ItemStack carriedItem, int[] data);

    void sendSlotChange(@NotNull UIPlayerHold uiPlayerHold, int slot, @NotNull ItemStack itemStack);

    void sendCarriedChange(@NotNull UIPlayerHold uiPlayerHold, @NotNull ItemStack itemStack);

    void sendDataChange(@NotNull UIPlayerHold uiPlayerHold, int id, int value);
}