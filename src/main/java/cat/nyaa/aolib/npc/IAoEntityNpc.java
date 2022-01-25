package cat.nyaa.aolib.npc;

import cat.nyaa.aolib.network.packet.AbstractWrappedPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundAddEntityPacket;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IAoEntityNpc {
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

    default AbstractWrappedPacket getAddEntityPacket(){
        return new WrappedClientboundAddEntityPacket(this);
    }

    EntityType getEntityType();

    default float getYHeadRot(){
        return 0.0F;
    }
}
