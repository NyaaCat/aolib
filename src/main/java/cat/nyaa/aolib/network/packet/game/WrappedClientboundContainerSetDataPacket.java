package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.jetbrains.annotations.NotNull;

public class WrappedClientboundContainerSetDataPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.WINDOW_DATA;

    protected WrappedClientboundContainerSetDataPacket(PacketContainer handle) throws IllegalArgumentException {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundContainerSetDataPacket(int containerId, int id, int value) {
        this(createPacket(containerId, id, value));
    }

    private static @NotNull PacketContainer createPacket(int containerId, int id, int value) {
        PacketContainer packetContainer = new PacketContainer(PACKET_TYPE);
        packetContainer.getIntegers().write(0, containerId);
        packetContainer.getIntegers().write(1, id);
        packetContainer.getIntegers().write(2, value);
        return packetContainer;
    }
}
