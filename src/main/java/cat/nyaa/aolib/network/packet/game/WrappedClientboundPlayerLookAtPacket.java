package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import org.jetbrains.annotations.NotNull;

//client
public class WrappedClientboundPlayerLookAtPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.LOOK_AT;

    protected WrappedClientboundPlayerLookAtPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundPlayerLookAtPacket(double x, double y, double z, int entityId, Anchor fromAnchor, Anchor toAnchor, boolean atEntity) {
        this(createPacket(x, y, z, entityId, fromAnchor, toAnchor, atEntity));
    }

    private static @NotNull PacketContainer createPacket(double x, double y, double z, int entityId, Anchor fromAnchor, Anchor toAnchor, boolean atEntity) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.LOOK_AT);
        packetContainer.getDoubles().write(0, x);
        packetContainer.getDoubles().write(1, y);
        packetContainer.getDoubles().write(2, z);
        packetContainer.getIntegers().write(0, entityId);
        packetContainer.getEnumModifier(Anchor.class, MinecraftReflection.getMinecraftClass("ArgumentAnchor$Anchor")).writeSafely(0, fromAnchor);
        packetContainer.getEnumModifier(Anchor.class, MinecraftReflection.getMinecraftClass("ArgumentAnchor$Anchor")).writeSafely(1, toAnchor);
        packetContainer.getBooleans().write(0, atEntity);
        return packetContainer;
    }

    public enum Anchor {
        FEET,
        EYES
    }
}

