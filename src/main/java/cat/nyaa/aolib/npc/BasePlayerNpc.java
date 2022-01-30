package cat.nyaa.aolib.npc;

import cat.nyaa.aolib.npc.data.NpcSkinData;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BasePlayerNpc implements IAoPlayerNpc {

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public int getEntityId() {
        return 0;
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public double getZ() {
        return 0;
    }

    @Override
    public float getYRot() {
        return 0;
    }

    @Override
    public float getXRot() {
        return 0;
    }

    @Override
    public @NotNull String getName() {
        return null;
    }

    @Override
    public @Nullable BaseComponent getDisplayName() {
        return null;
    }

    @Override
    public NpcSkinData getSkinData() {
        return null;
    }
}
