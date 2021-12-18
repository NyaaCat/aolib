package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WrappedClientboundContainerSetContentPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.WINDOW_ITEMS;

    public WrappedClientboundContainerSetContentPacket(int windowId, int stateId, @NotNull List<ItemStack> items, @NotNull ItemStack carriedItem) {
        this(createPacket(windowId, stateId, items, carriedItem));
    }

    public WrappedClientboundContainerSetContentPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    private static @NotNull PacketContainer createPacket(int windowId, int stateId, @NotNull List<ItemStack> items, @NotNull ItemStack carriedItem) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.WINDOW_ITEMS);
        packetContainer.getIntegers().write(0, windowId);
        packetContainer.getIntegers().write(1, stateId);
        packetContainer.getItemListModifier().write(0, items);
        packetContainer.getItemModifier().write(0, carriedItem);
        return packetContainer;
    }


}
