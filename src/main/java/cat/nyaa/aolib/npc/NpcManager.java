package cat.nyaa.aolib.npc;

import cat.nyaa.aolib.network.packet.game.WrappedClientboundPlayerInfoPacket;
import cat.nyaa.aolib.npc.data.NpcSkinData;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class NpcManager {

    public void sendAddNpc(Player target, IAoEntityNpc npc) {
        try {
            if (npc instanceof IAoPlayerNpc)
                new WrappedClientboundPlayerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER, getNpcPlayerInfoData((IAoPlayerNpc) npc)).sendServerPacket(target);

            npc.getAddEntityPacket().sendServerPacket(target);

            if (npc instanceof IAoPlayerNpc)
                new WrappedClientboundPlayerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, getNpcPlayerInfoData((IAoPlayerNpc) npc)).sendServerPacket(target);

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private PlayerInfoData getNpcPlayerInfoData(IAoPlayerNpc playerNpc) {
        return new PlayerInfoData(
                getPlayerNpcGameProfile(playerNpc),
                playerNpc.getLatency(),
                EnumWrappers.NativeGameMode.fromBukkit(playerNpc.getGameMode()),
                ComponentConverter.fromBaseComponent(playerNpc.getDisplayName())
        );
    }

    private WrappedGameProfile getPlayerNpcGameProfile(IAoPlayerNpc playerNpc) {
        WrappedGameProfile result = new WrappedGameProfile(playerNpc.getUUID(), playerNpc.getName());
        playerNpc.getPropertyList().stream().filter(Objects::nonNull).forEach(property -> result.getProperties().put(property.getName(),property));
        return result;
    }
}
