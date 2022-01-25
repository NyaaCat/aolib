package cat.nyaa.aolib.aoui.utils;

import cat.nyaa.nyaacore.utils.EntityUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class NpcUtils {

    public static final String UUID_PREFIX_NPC_PLAYER = "AoNpcPlayer:";

    @Contract("_ -> new")
    public static @NotNull UUID createNpcPlayerUUID(String npcId) {
        return UUID.nameUUIDFromBytes((UUID_PREFIX_NPC_PLAYER + npcId).getBytes(StandardCharsets.UTF_8));
    }

    public static int nextEntityId() {
        return EntityUtils.getNewEntityId();
    }

}