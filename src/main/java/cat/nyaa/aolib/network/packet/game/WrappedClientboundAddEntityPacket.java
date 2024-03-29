package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import cat.nyaa.aolib.npc.IAoEntityNpc;
import cat.nyaa.aolib.npc.IAoLivingEntityNpc;
import cat.nyaa.aolib.utils.NetworkUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WrappedClientboundAddEntityPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.SPAWN_ENTITY;

    protected WrappedClientboundAddEntityPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundAddEntityPacket(IAoEntityNpc aoNpc) {
        this(createPacket(
                aoNpc.getEntityId(),
                aoNpc.getUUID(),
                aoNpc.getEntityType(),
                aoNpc.getX(),
                aoNpc.getY(),
                aoNpc.getZ(),
                NetworkUtils.power2acceleration(aoNpc.getXPower()),
                NetworkUtils.power2acceleration(aoNpc.getYPower()),
                NetworkUtils.power2acceleration(aoNpc.getZPower()),
                NetworkUtils.rot2byte(aoNpc.getXRot()),
                NetworkUtils.rot2byte(aoNpc.getYRot()),
                NetworkUtils.rot2byte(aoNpc.getYHeadRot()),
                aoNpc.getEntitySpawnData()
        ));
    }

    public WrappedClientboundAddEntityPacket(IAoLivingEntityNpc livingEntityNpc) {
        this((IAoEntityNpc) livingEntityNpc);
    }

    private static @NotNull PacketContainer createPacket(int id, UUID uuid, EntityType type, double x, double y, double z, int xa, int ya, int za, byte xRot, byte yRot, byte yHeadRot, int data) {

        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        packetContainer.getIntegers().write(0, id);
        packetContainer.getUUIDs().write(0, uuid);
        packetContainer.getEntityTypeModifier().write(0, type);

        packetContainer.getDoubles().write(0, x);
        packetContainer.getDoubles().write(1, y);
        packetContainer.getDoubles().write(2, z);

        packetContainer.getIntegers().write(1, xa);
        packetContainer.getIntegers().write(2, ya);
        packetContainer.getIntegers().write(3, za);

        packetContainer.getBytes().write(0, xRot);
        packetContainer.getBytes().write(1, yRot);
        packetContainer.getBytes().write(2, yHeadRot);


        packetContainer.getIntegers().write(4, data);
        return packetContainer;
    }
}
