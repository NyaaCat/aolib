package cat.nyaa.aolib.network.packet.game;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WrappedClientboundPlayerInfoPacket extends AbstractWrappedPacket {
    public final static PacketType PACKET_TYPE = PacketType.Play.Server.PLAYER_INFO;

    protected WrappedClientboundPlayerInfoPacket(PacketContainer handle) {
        super(handle, PACKET_TYPE);
    }

    public WrappedClientboundPlayerInfoPacket(EnumWrappers.PlayerInfoAction action, List<PlayerInfoData> playerInfoDataList) {
        this(createPacket(action, playerInfoDataList));
    }

    public WrappedClientboundPlayerInfoPacket(EnumWrappers.PlayerInfoAction action, PlayerInfoData... playerInfoData) {
        this(createPacket(action, Lists.newArrayList(playerInfoData)));
    }


    public static @NotNull PacketContainer createPacket(EnumWrappers.PlayerInfoAction action, List<PlayerInfoData> playerInfoDataList) {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packetContainer.getPlayerInfoAction().write(0, action);
        packetContainer.getPlayerInfoDataLists().write(0, playerInfoDataList);
        return packetContainer;
    }
}
