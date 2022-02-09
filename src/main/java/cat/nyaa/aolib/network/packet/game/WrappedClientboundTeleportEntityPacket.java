package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import cat.nyaa.aolib.npc.IAoEntityNpc;
import cat.nyaa.aolib.utils.NetworkUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.jetbrains.annotations.NotNull;

public class WrappedClientboundTeleportEntityPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.ENTITY_TELEPORT;

    protected WrappedClientboundTeleportEntityPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundTeleportEntityPacket(int id, double x, double y, double z, byte yRot, byte xRot, boolean onGround) {
        this(createPacket(id, x, y, z, yRot, xRot, onGround));
    }

    public WrappedClientboundTeleportEntityPacket(IAoEntityNpc entityNpc) {
        this(entityNpc.getEntityId(), entityNpc.getX(), entityNpc.getY(), entityNpc.getZ(), NetworkUtils.rot2byte(entityNpc.getYRot()), NetworkUtils.rot2byte(entityNpc.getXRot()), entityNpc.isOnGround());
    }

    private static @NotNull PacketContainer createPacket(int id, double x, double y, double z, byte yRot, byte xRot, boolean onGround) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packetContainer.getIntegers().write(0, id);
        packetContainer.getDoubles().write(0, x);
        packetContainer.getDoubles().write(1, y);
        packetContainer.getDoubles().write(2, z);
        packetContainer.getBytes().write(0, yRot);
        packetContainer.getBytes().write(1, xRot);
        packetContainer.getBooleans().write(0, onGround);
        return packetContainer;
    }
}
