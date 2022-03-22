package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import cat.nyaa.aolib.npc.IAoLivingEntityNpc;
import cat.nyaa.aolib.utils.NetworkUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WrappedClientboundAddMobPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.SPAWN_ENTITY_LIVING;

    protected WrappedClientboundAddMobPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundAddMobPacket(IAoLivingEntityNpc livingEntityNpc) {
        this(createPacket(
                livingEntityNpc.getEntityId(),
                livingEntityNpc.getUUID(),
                NetworkUtils.tryToGetEntityTypeId(livingEntityNpc.getEntityType()),
                livingEntityNpc.getX(),
                livingEntityNpc.getY(),
                livingEntityNpc.getZ(),
                NetworkUtils.power2acceleration(livingEntityNpc.getXPower()),
                NetworkUtils.power2acceleration(livingEntityNpc.getYPower()),
                NetworkUtils.power2acceleration(livingEntityNpc.getZPower()),
                NetworkUtils.rot2byte(livingEntityNpc.getYRot()),
                NetworkUtils.rot2byte(livingEntityNpc.getXRot()),
                NetworkUtils.rot2byte(livingEntityNpc.getYHeadRot())
        ));
    }

    private static @NotNull PacketContainer createPacket(int entityId, UUID uuid, int typeID, double x, double y, double z, int xd, int yd, int zd, byte yRot, byte xRot, byte yHeadRot) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getUUIDs().write(0, uuid);
        packetContainer.getIntegers().write(1, typeID);
        packetContainer.getDoubles().write(0, x);
        packetContainer.getDoubles().write(1, y);
        packetContainer.getDoubles().write(2, z);

        packetContainer.getIntegers().write(2, xd);
        packetContainer.getIntegers().write(3, yd);
        packetContainer.getIntegers().write(4, zd);

        packetContainer.getBytes().write(0, yRot);
        packetContainer.getBytes().write(1, xRot);
        packetContainer.getBytes().write(2, yHeadRot);
        return packetContainer;
    }
}
