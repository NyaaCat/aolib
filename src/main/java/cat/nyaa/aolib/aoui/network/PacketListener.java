package cat.nyaa.aolib.aoui.network;

import cat.nyaa.aolib.aoui.UIManager;
import cat.nyaa.aolib.network.packet.game.WrappedServerboundContainerButtonClickPacket;
import cat.nyaa.aolib.network.packet.game.WrappedServerboundContainerClickPacket;
import cat.nyaa.aolib.network.packet.game.WrappedServerboundContainerClosePacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PacketListener extends PacketAdapter {
    protected UIManager uiManager;

    public PacketListener(Plugin plugin, UIManager uiManager) {
        super(plugin, PacketType.Play.Client.CLOSE_WINDOW, PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.ENCHANT_ITEM);
        this.uiManager = uiManager;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketType packetType = event.getPacketType();
        Player player = event.getPlayer();
        if (PacketType.Play.Client.CLOSE_WINDOW.equals(packetType)) {
            WrappedServerboundContainerClosePacket wrappedPacket = new WrappedServerboundContainerClosePacket(event.getPacket());
            uiManager.handleWindowClose(player, wrappedPacket);
        } else if (PacketType.Play.Client.WINDOW_CLICK.equals(packetType)) {
            WrappedServerboundContainerClickPacket wrappedPacket = new WrappedServerboundContainerClickPacket(event.getPacket());
            uiManager.handleWindowClick(player, wrappedPacket);
        } else if (PacketType.Play.Client.ENCHANT_ITEM.equals(packetType)) {
            WrappedServerboundContainerButtonClickPacket wrappedPacket = new WrappedServerboundContainerButtonClickPacket(event.getPacket());
            uiManager.handleWindowButtonClick(player, wrappedPacket);
        } else {
            throw new IllegalStateException("Unexpected value: " + event.getPacketType());
        }
    }

    @Override
    public void onPacketSending(PacketEvent event) {
    }
}
