package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.aoui.utils.NetworkUtils;
import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import cat.nyaa.aolib.npc.IAoPlayerNpc;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WrappedClientboundAddPlayerPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.NAMED_ENTITY_SPAWN;

    protected WrappedClientboundAddPlayerPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundAddPlayerPacket(int entityId, UUID playerId, double x, double y, double z, byte yRot, byte xRot) {
        this(createPacket(entityId, playerId, x, y, z, yRot, xRot));
    }

    public WrappedClientboundAddPlayerPacket(IAoPlayerNpc playerNpc) {
        this(createPacket(
                playerNpc.getEntityId(),
                playerNpc.getUUID(),
                playerNpc.getX(),
                playerNpc.getY(),
                playerNpc.getZ(),
                NetworkUtils.rot2byte(playerNpc.getYRot()),
                NetworkUtils.rot2byte(playerNpc.getXRot())
        ));
    }

    private static @NotNull PacketContainer createPacket(int entityId, UUID playerId, double x, double y, double z, byte yRot, byte xRot) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getUUIDs().write(0, playerId);
        packetContainer.getDoubles().write(0, x);
        packetContainer.getDoubles().write(1, y);
        packetContainer.getDoubles().write(2, z);
        packetContainer.getBytes().write(0, yRot);
        packetContainer.getBytes().write(1, xRot);
        return packetContainer;
    }
}
