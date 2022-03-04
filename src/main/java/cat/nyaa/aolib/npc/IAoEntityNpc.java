package cat.nyaa.aolib.npc;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundAddEntityPacket;
import cat.nyaa.aolib.npc.data.NpcEntityData;
import cat.nyaa.aolib.npc.data.NpcInteractActionData;
import cat.nyaa.nyaacore.utils.EntityUtils;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface IAoEntityNpc {
    /**
     * todo Not completed
     * Each tick is called when the NPC is active.
     * It is also called once at 128 tick intervals when the NPC is loaded.
     *
     * @param tickNum  Number of ticks passed from NpcManager
     * @param isActive is NPC active
     */
    default void onNpcTick(long tickNum, boolean isActive) {//todo NpcTick

    }

    /**
     * A unique name for the index, usually using the primary key in the database.
     *
     * @return unique name
     */
    @NotNull
    String getUniqueNpcName();

    default int getUpdateInterval() {
        return EntityUtils.getUpdateInterval(getEntityType());
    }

    default int getClientTrackingRange() {
        return EntityUtils.getClientTrackingRange(getEntityType());
    }

    //todo World getWorld();

    @NotNull
    UUID getUUID();

    int getEntityId();

    double getX();

    double getY();

    double getZ();

    //EntityType

    default int getEntitySpawnData() {
        return 0;
    }

    default double getXPower() {
        return 0.0d;
    }

    default double getYPower() {
        return 0.0d;
    }

    default double getZPower() {
        return 0.0d;
    }

    default int getChunkX() {
        return ((int) Math.floor(getX())) >> 4;
    }

    default int getChunkZ() {
        return ((int) Math.floor(getZ())) >> 4;
    }

    float getYRot();

    float getXRot();

    @NotNull
    String getName();

    @Nullable
    BaseComponent getDisplayName();

    default AbstractWrappedPacket getAddEntityPacket() {
        return new WrappedClientboundAddEntityPacket(this);
    }

    EntityType getEntityType();

    default float getYHeadRot() {
        return 0.0F;
    }


    default List<NpcEntityData> getNpcEntityDataList() {
        return Lists.newArrayList();
    }

    default void onLoad() {
    }

    default void preSendAdd(Player target, IAoEntityNpc npc) {

    }

    default void preSendRemove(Player target, IAoEntityNpc npc) {

    }

    default void onUnload() {
    }

    default boolean isOnGround() {
        return true;
    }

    default void onInteract(int entityId, NpcInteractActionData npcInteractActionData, boolean usingSecondaryAction){

    }
}
