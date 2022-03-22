package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WrappedClientboundSetEquipmentPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.ENTITY_EQUIPMENT;

    protected WrappedClientboundSetEquipmentPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundSetEquipmentPacket(int entityId, List<Pair<EnumWrappers.ItemSlot, ItemStack>> slots) {
        this(createPacket(entityId, slots));
    }

    private static @NotNull PacketContainer createPacket(int entityId, List<Pair<EnumWrappers.ItemSlot, ItemStack>> slots) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getSlotStackPairLists().write(0, slots);
        return packetContainer;
    }

}
