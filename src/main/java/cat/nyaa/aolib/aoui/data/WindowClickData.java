package cat.nyaa.aolib.aoui.data;

import cat.nyaa.aolib.network.data.DataClickType;
import cat.nyaa.aolib.network.packet.game.WrappedServerboundContainerClickPacket;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record WindowClickData(int containerId, int stateId, int slotNum, int buttonNum, DataClickType clickType,
                              ItemStack carriedItem, Map<Integer, ItemStack> changedSlots) {
    private static final int MAX_SLOT_COUNT = 128;

    public WindowClickData(@NotNull WrappedServerboundContainerClickPacket wrappedPacket) {
        this(wrappedPacket.getContainerId(), wrappedPacket.getStateId(), wrappedPacket.getSlotNum(), wrappedPacket.getButtonNum(), wrappedPacket.getClickType(), wrappedPacket.getCarriedItem(), wrappedPacket.getChangedSlots());
    }
}
