package cat.nyaa.aolib.npc;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundAddPlayerPacket;
import cat.nyaa.aolib.npc.data.NpcEntityData;
import cat.nyaa.aolib.npc.data.NpcPlayerPropertyData;
import cat.nyaa.aolib.npc.data.NpcSkinData;
import cat.nyaa.aolib.utils.EntityDataUtils;
import com.google.common.collect.Lists;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

    @Nullable NpcSkinData getSkinData();

    default List<NpcPlayerPropertyData> getNpcPlayerPropertyDataList() {
        List<NpcPlayerPropertyData> result = Lists.newArrayList();
        NpcSkinData npcSkinData;
        if ((npcSkinData = getSkinData()) != null) {
            result.add(new NpcPlayerPropertyData("textures", npcSkinData.texture_value(), npcSkinData.texture_signature()));
        }
        return result;
    }

    default AbstractWrappedPacket getAddEntityPacket() {
        return new WrappedClientboundAddPlayerPacket(this);
    }

    default List<NpcEntityData> getNpcEntityDataList() {
        var result = new ArrayList<>(IAoLivingEntityNpc.super.getNpcEntityDataList());
        result.add(new NpcEntityData(EntityDataUtils.PLAYER_ENTITY_DATA_PLAYER_MODE_CUSTOMISATION_ID, (byte) 0x7F, Byte.class, false));
        return result;
    }
}
