package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import cat.nyaa.aolib.utils.NetworkUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.jetbrains.annotations.NotNull;

public class WrappedClientboundSetEntityMotionPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.ENTITY_VELOCITY;

    protected WrappedClientboundSetEntityMotionPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);

    }

    public WrappedClientboundSetEntityMotionPacket(int id, int xa, int ya, int za) {
        this(createPacket(id, xa, ya, za));
    }

    public WrappedClientboundSetEntityMotionPacket(int id, double xPower, double yPower, double zPower) {
        this(id, NetworkUtils.power2acceleration(xPower), NetworkUtils.power2acceleration(yPower), NetworkUtils.power2acceleration(zPower));
    }

    private static @NotNull PacketContainer createPacket(int id, int xa, int ya, int za) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_VELOCITY);
        packetContainer.getIntegers().write(0, id);
        packetContainer.getIntegers().write(1, xa);
        packetContainer.getIntegers().write(2, ya);
        packetContainer.getIntegers().write(3, za);
        return packetContainer;
    }
}
