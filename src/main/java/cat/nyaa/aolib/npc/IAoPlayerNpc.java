package cat.nyaa.aolib.npc;

import org.bukkit.entity.EntityType;

public interface IAoPlayerNpc extends IAoLivingEntityNpc {
    int getPing();

    int getGameMode();

    @Override
    default EntityType getEntityType() {
        return EntityType.PLAYER;
    }
}
