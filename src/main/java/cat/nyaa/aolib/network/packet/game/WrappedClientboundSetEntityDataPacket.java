package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WrappedClientboundSetEntityDataPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.ENTITY_METADATA;

    protected WrappedClientboundSetEntityDataPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundSetEntityDataPacket(int entityId, List<WrappedWatchableObject> watchableObjects) {
        this(createPacket(entityId, watchableObjects));
    }

    private static @NotNull PacketContainer createPacket(int id, List<WrappedWatchableObject> watchableObjects) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        packetContainer.getIntegers().write(0, id);
        packetContainer.getWatchableCollectionModifier().write(0, watchableObjects);
        return packetContainer;
    }
}
