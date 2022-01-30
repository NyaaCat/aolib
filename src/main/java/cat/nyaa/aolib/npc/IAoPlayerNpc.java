package cat.nyaa.aolib.npc;

import cat.nyaa.aolib.npc.data.NpcSkinData;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.Lists;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;

import java.util.List;

public interface IAoPlayerNpc extends IAoLivingEntityNpc {
    default int getLatency() {
        return 0;
    }//ping

    default GameMode getGameMode() {
        return GameMode.CREATIVE;
    }

    @Override
    default EntityType getEntityType() {
        return EntityType.PLAYER;
    }

    NpcSkinData getSkinData();

    default List<WrappedSignedProperty> getPropertyList() {
        List<WrappedSignedProperty> result = Lists.newArrayList();
        NpcSkinData npcSkinData;
        if ((npcSkinData = getSkinData()) != null) {
            result.add(new WrappedSignedProperty("textures", npcSkinData.texture_value(), npcSkinData.texture_signature()));
        }
        return result;
    }
}
