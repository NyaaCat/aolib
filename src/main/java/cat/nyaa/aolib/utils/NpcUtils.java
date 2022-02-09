package cat.nyaa.aolib.utils;

import cat.nyaa.aolib.npc.data.NpcSkinData;
import cat.nyaa.nyaacore.utils.EntityUtils;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class NpcUtils {

    public static final String UUID_PREFIX_NPC_PLAYER = "AoNpcPlayer:";

    @Contract("_ -> new")
    public static @NotNull UUID createNpcPlayerUUID(String npcId) {
        return UUID.nameUUIDFromBytes((UUID_PREFIX_NPC_PLAYER + npcId).getBytes(StandardCharsets.UTF_8));
    }

    @Nullable
    public static NpcSkinData getPlayerSkinData(Player player) {
        var propertiesMap = WrappedGameProfile.fromPlayer(player).getProperties();
        NpcSkinData skin = null;
        if (propertiesMap.containsKey("textures")) {
            var textures = propertiesMap.get("textures");
            for (WrappedSignedProperty texture : textures) {
                skin = new NpcSkinData(texture.getValue(), texture.getSignature());
            }
        }
        return skin;
    }

    public static int nextEntityId() {
        return EntityUtils.getNewEntityId();
    }

}