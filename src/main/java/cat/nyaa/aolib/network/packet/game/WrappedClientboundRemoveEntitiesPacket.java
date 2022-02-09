package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.primitives.Ints;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WrappedClientboundRemoveEntitiesPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.ENTITY_DESTROY;

    protected WrappedClientboundRemoveEntitiesPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    private static @NotNull PacketContainer createPacket(List<Integer> entityIds) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        packetContainer.getIntLists().write(0, entityIds);
        return packetContainer;
    }

    public WrappedClientboundRemoveEntitiesPacket(List<Integer> entityIdList) {
        this(createPacket(entityIdList));
    }
    public WrappedClientboundRemoveEntitiesPacket(int... entityId) {
        this(Ints.asList(entityId));
    }
}
