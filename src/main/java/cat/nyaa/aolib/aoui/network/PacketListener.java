package cat.nyaa.aolib.aoui.network;

import cat.nyaa.aolib.aoui.UIManager;
import cat.nyaa.aolib.network.packet.game.WrappedServerboundContainerButtonClickPacket;
import cat.nyaa.aolib.network.packet.game.WrappedServerboundContainerClickPacket;
import cat.nyaa.aolib.network.packet.game.WrappedServerboundContainerClosePacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.Callable;

public class PacketListener extends PacketAdapter {
    protected UIManager uiManager;

    public PacketListener(Plugin plugin, UIManager uiManager) {
        super(plugin, PacketType.Play.Client.CLOSE_WINDOW, PacketType.Play.Client.WINDOW_CLICK, PacketType.Play.Client.ENCHANT_ITEM);
        this.uiManager = uiManager;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketType packetType = event.getPacketType();
        Callable<Boolean> callable = () -> {
            Player player = event.getPlayer();
            if (PacketType.Play.Client.CLOSE_WINDOW.equals(packetType)) {
                WrappedServerboundContainerClosePacket wrappedPacket = new WrappedServerboundContainerClosePacket(event.getPacket());
                return uiManager.handleWindowClose(player, wrappedPacket);
            } else if (PacketType.Play.Client.WINDOW_CLICK.equals(packetType)) {
                WrappedServerboundContainerClickPacket wrappedPacket = new WrappedServerboundContainerClickPacket(event.getPacket());
                return uiManager.handleWindowClick(player, wrappedPacket);
            } else if (PacketType.Play.Client.ENCHANT_ITEM.equals(packetType)) {
                WrappedServerboundContainerButtonClickPacket wrappedPacket = new WrappedServerboundContainerButtonClickPacket(event.getPacket());
                return uiManager.handleWindowButtonClick(player, wrappedPacket);
            } else {
                throw new IllegalStateException("Unexpected value: " + event.getPacketType());
            }
        };
        Boolean cancel = false;
        if (event.isAsync()) {
            try {
                cancel = Bukkit.getScheduler().callSyncMethod(getPlugin(), callable).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                cancel = callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (cancel != null && cancel && !event.isReadOnly() && !event.isCancelled()) {
            event.setCancelled(true);
        }

    }

    @Override
    public void onPacketSending(PacketEvent event) {
    }
}
