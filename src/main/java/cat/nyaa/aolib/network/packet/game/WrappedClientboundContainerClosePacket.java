package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.jetbrains.annotations.NotNull;

public class WrappedClientboundContainerClosePacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.CLOSE_WINDOW;

    public WrappedClientboundContainerClosePacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundContainerClosePacket(int windowId) {
        this(createPacket(windowId));
    }

    public static @NotNull PacketContainer createPacket(int windowId) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.CLOSE_WINDOW);
        packetContainer.getIntegers().write(0, windowId);
        return packetContainer;
    }
}
