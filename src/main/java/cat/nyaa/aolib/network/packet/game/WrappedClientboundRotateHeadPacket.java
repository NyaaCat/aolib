package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrappedClientboundRotateHeadPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.ENTITY_HEAD_ROTATION;

    protected WrappedClientboundRotateHeadPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundRotateHeadPacket(int entityId, byte yHeadRot) {
        this(createPacket(entityId, yHeadRot));
    }

    private static PacketContainer createPacket(int entityId, byte yHeadRot) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getBytes().write(0, yHeadRot);
        return packetContainer;
    }
}
