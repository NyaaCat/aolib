package cat.nyaa.aolib.network.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public interface IPacket {

    static PacketType getPacketType() {
        return null;
    }

    PacketType getHandlePacketType();

    PacketContainer getPacket();
}
 