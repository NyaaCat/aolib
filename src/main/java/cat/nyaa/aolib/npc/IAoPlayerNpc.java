package cat.nyaa.aolib.npc;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundAddPlayerPacket;
import cat.nyaa.aolib.npc.data.NpcSkinData;
import cat.nyaa.aolib.utils.EntityDataUtils;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
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

    default List<WrappedSignedProperty> getPropertyList() {
        List<WrappedSignedProperty> result = Lists.newArrayList();
        NpcSkinData npcSkinData;
        if ((npcSkinData = getSkinData()) != null) {
            result.add(new WrappedSignedProperty("textures", npcSkinData.texture_value(), npcSkinData.texture_signature()));
        }
        return result;
    }

    default AbstractWrappedPacket getAddEntityPacket() {
        return new WrappedClientboundAddPlayerPacket(this);
    }

    @Override
    default List<WrappedWatchableObject> getWatchableObjectList() {
        var result = new ArrayList<>(IAoLivingEntityNpc.super.getWatchableObjectList());
        result.add(new WrappedWatchableObject(new WrappedDataWatcher.WrappedDataWatcherObject(EntityDataUtils.PLAYER_ENTITY_DATA_PLAYER_MODE_CUSTOMISATION_ID, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x7F));
        return result;
    }
}
