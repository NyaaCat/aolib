package cat.nyaa.aolib.aoui.network;

import cat.nyaa.aolib.aoui.UIManager;
import cat.nyaa.aolib.network.packet.game.WrappedServerboundContainerButtonClickPacket;
import cat.nyaa.aolib.network.packet.game.WrappedServerboundContainerClickPacket;
import cat.nyaa.aolib.network.packet.game.WrappedServerboundContainerClosePacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class UIPacketListener extends PacketAdapter {
    protected UIManager uiManager;

    public UIPacketListener(Plugin plugin, UIManager uiManager) {
        super(plugin, PacketType.Play.Client.CLOSE_WINDOW, PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.ENCHANT_ITEM);
        this.uiManager = uiManager;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketType packetType = event.getPacketType();
        UUID playerId = event.getPlayer().getUniqueId();

        boolean cancel;
        if (PacketType.Play.Client.CLOSE_WINDOW.equals(packetType)) {
            WrappedServerboundContainerClosePacket wrappedPacket = new WrappedServerboundContainerClosePacket(event.getPacket());
            cancel = uiManager.handleWindowCloseAsync(playerId, wrappedPacket);
        } else if (PacketType.Play.Client.WINDOW_CLICK.equals(packetType)) {
            WrappedServerboundContainerClickPacket wrappedPacket = new WrappedServerboundContainerClickPacket(event.getPacket());
            cancel = uiManager.handleWindowClickAsync(playerId, wrappedPacket);
        } else if (PacketType.Play.Client.ENCHANT_ITEM.equals(packetType)) {
            WrappedServerboundContainerButtonClickPacket wrappedPacket = new WrappedServerboundContainerButtonClickPacket(event.getPacket());
            cancel = uiManager.handleWindowButtonClickAsync(playerId, wrappedPacket);
        } else {
            throw new IllegalStateException("Unexpected value: " + event.getPacketType());
        }

        if (cancel && !event.isReadOnly() && !event.isCancelled()) {
            event.setCancelled(true);
        }

    }

    @Override
    public void onPacketSending(PacketEvent event) {
    }
}
