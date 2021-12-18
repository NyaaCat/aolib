package cat.nyaa.aolib.network.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public interface IPacket {

    PacketType getHandlePacketType();

    static PacketType getPacketType() {
        return null;
    }

    PacketContainer getPacket();
}
 