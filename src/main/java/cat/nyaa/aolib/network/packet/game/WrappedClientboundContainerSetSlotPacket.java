package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WrappedClientboundContainerSetSlotPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.SET_SLOT;

    public WrappedClientboundContainerSetSlotPacket(PacketContainer handle) throws IllegalArgumentException {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundContainerSetSlotPacket(int containerId, int stateId, int slot, ItemStack itemStack) {
        this(createPacket(containerId, stateId, slot, itemStack));
    }

    private static @NotNull PacketContainer createPacket(int containerId, int stateId, int slot, ItemStack itemStack) {
        PacketContainer packetContainer = new PacketContainer(PACKET_TYPE);
        packetContainer.getIntegers().write(0, containerId);
        packetContainer.getIntegers().write(1, stateId);
        packetContainer.getIntegers().write(2, slot);
        packetContainer.getItemModifier().write(0, itemStack);
        return packetContainer;
    }
}
