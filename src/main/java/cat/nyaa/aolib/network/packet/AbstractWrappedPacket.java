package cat.nyaa.aolib.network.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class AbstractWrappedPacket implements IPacket {
    protected AbstractWrappedPacket(PacketContainer handle, PacketType packetType) throws IllegalArgumentException {
        if (!handle.getType().equals(packetType)) throw new IllegalArgumentException();
        this.handle = handle;
    }

    private final PacketContainer handle;

    public PacketContainer getHandle() {
        return this.handle;
    }

    @Override
    public PacketType getHandlePacketType() {
        return handle.getType();
    }

    @Override
    public PacketContainer getPacket() {
        return getHandle();
    }

    public void sendServerPacket(Player player) throws InvocationTargetException {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, getPacket());
    }
}
