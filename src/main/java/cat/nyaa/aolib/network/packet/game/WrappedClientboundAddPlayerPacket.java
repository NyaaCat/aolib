package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
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

    public WrappedClientboundAddPlayerPacket(int entityId, UUID playerId, Location location) {
        this(createPacket(entityId, playerId, location.getX(), location.getY(), location.getZ(), (byte) ((int) (location.getPitch() * 256.0F / 360.0F)), (byte) ((int) (location.getYaw() * 256.0F / 360.0F))));
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
