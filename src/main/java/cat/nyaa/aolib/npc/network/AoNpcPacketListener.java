package cat.nyaa.aolib.npc.network;

import cat.nyaa.aolib.network.packet.game.WrappedServerboundInteractPacket;
import cat.nyaa.aolib.npc.NpcManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;

public class AoNpcPacketListener extends PacketAdapter {
    private final NpcManager npcManager;

    public AoNpcPacketListener(Plugin plugin, NpcManager npcManager) {
        super(plugin, PacketType.Play.Client.USE_ENTITY);
        this.npcManager = npcManager;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        PacketType packetType = event.getPacketType();

        if (PacketType.Play.Client.USE_ENTITY.equals(packetType)) {
            npcManager.handleInteractAsync(event.getPlayer(), new WrappedServerboundInteractPacket(event.getPacket()));
        } else {
            throw new IllegalStateException("Unexpected value: " + event.getPacketType());
        }
        //todo cancel packet
    }

    @Override
    public void onPacketSending(PacketEvent event) {
    }
}
