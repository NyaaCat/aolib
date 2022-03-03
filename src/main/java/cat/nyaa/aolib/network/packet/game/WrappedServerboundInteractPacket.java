package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;

public class WrappedServerboundInteractPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Client.USE_ENTITY;

    public WrappedServerboundInteractPacket(PacketContainer handle) throws IllegalArgumentException {
        super(handle, PACKET_TYPE);
    }

    public int getEntityId() {
        return getHandle().getIntegers().read(0);
    }

    public WrappedEnumEntityUseAction getAction() {
        return getHandle().getEnumEntityUseActions().read(0);
    }

    public boolean getUsingSecondaryAction() { //Sneaking
        return getHandle().getBooleans().read(0);
    }
}
