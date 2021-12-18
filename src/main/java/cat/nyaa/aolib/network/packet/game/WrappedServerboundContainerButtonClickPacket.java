package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrappedServerboundContainerButtonClickPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Client.ENCHANT_ITEM;

    public WrappedServerboundContainerButtonClickPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public int getContainerId() {
        return getPacket().getIntegers().read(0);
    }

    public int getButtonId() {
        return getPacket().getIntegers().read(1);
    }


}
