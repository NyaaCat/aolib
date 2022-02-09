package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
//client
public class WrappedClientboundPlayerPositionPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.POSITION;

    protected WrappedClientboundPlayerPositionPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundPlayerPositionPacket(double x, double y, double z, float yRot, float xRot, int teleportId) {
        this(createPacket(x, y, z, yRot, xRot, Collections.emptySet(), teleportId, false));
    }

    private static @NotNull PacketContainer createPacket(double x, double y, double z, float yRot, float xRot, Set<?> relativeArguments, int id, boolean dismountVehicle) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.POSITION);
        packetContainer.getDoubles().write(0, x);
        packetContainer.getDoubles().write(1, y);
        packetContainer.getDoubles().write(2, z);
        packetContainer.getFloat().write(0, yRot);
        packetContainer.getFloat().write(1, xRot);
        //packetContainer.getSets() todo RelativeArgument
        packetContainer.getSpecificModifier(Set.class).write(0, relativeArguments);// Collections.emptySet()
        packetContainer.getIntegers().write(0, id);
        packetContainer.getBooleans().write(0, dismountVehicle);
        return packetContainer;
    }
}
