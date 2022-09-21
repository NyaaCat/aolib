package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import cat.nyaa.nyaacore.utils.RegistryUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.jetbrains.annotations.NotNull;

public class WrappedClientboundOpenScreenPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.OPEN_WINDOW;


    protected WrappedClientboundOpenScreenPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundOpenScreenPacket(int windowId, int type, WrappedChatComponent title) {
        this(createPacket(windowId, type, title));
    }

    private static @NotNull PacketContainer createPacket(int windowId, int type, WrappedChatComponent title) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.OPEN_WINDOW);
        packetContainer.getIntegers().write(0, windowId);
        // Containers
        packetContainer.getModifier().write(1, RegistryUtils.getMenuTypeById(type));
        packetContainer.getChatComponents().write(0, title);
        return packetContainer;

    }

}
