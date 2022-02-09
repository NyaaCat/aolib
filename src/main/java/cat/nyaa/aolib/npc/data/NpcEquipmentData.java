package cat.nyaa.aolib.npc.data;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public record NpcEquipmentData(
        @Nullable ItemStack mainHand,
        @Nullable ItemStack offHand,
        @Nullable ItemStack feet,
        @Nullable ItemStack legs,
        @Nullable ItemStack chest,
        @Nullable ItemStack head
) {
    public NpcEquipmentData() {
        this(null, null, null, null, null, null);
    }
}
