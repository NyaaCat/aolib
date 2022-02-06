package cat.nyaa.aolib.npc;

import cat.nyaa.aolib.npc.data.NpcSkinData;
import cat.nyaa.aolib.utils.NpcUtils;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BasePlayerNpc implements IAoPlayerNpc {
    public static String npcId = "AOLIB_TEST";
    public static UUID npcPlayerUUID = NpcUtils.createNpcPlayerUUID(npcId);
    public static int entityID = NpcUtils.nextEntityId();
    private final org.bukkit.Location Location;
    private final String name;
    @Nullable
    private final NpcSkinData skinData;
    private final String displayName;

    public BasePlayerNpc(Player clone) {
        this.Location = clone.getLocation();
        this.name = clone.getName() + "Npc";
        this.displayName = clone.getDisplayName();
        var propertiesMap = WrappedGameProfile.fromPlayer(clone).getProperties();
        NpcSkinData skin = null;
        if (propertiesMap.containsKey("textures")) {
            var textures = propertiesMap.get("textures");
            for (WrappedSignedProperty texture : textures) {
                skin = new NpcSkinData(texture.getValue(), texture.getSignature());
            }
        }
        skinData = skin;

    }

    @Override
    public @NotNull UUID getUUID() {
        return npcPlayerUUID;
    }

    @Override
    public int getEntityId() {
        return entityID;
    }

    @Override
    public double getX() {
        return Location.getX();
    }

    @Override
    public double getY() {
        return Location.getY();
    }

    @Override
    public double getZ() {
        return Location.getZ();
    }

    @Override
    public float getYRot() {
        return Location.getYaw();
    }

    @Override
    public float getXRot() {
        return Location.getPitch();
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @Nullable BaseComponent getDisplayName() {
        return new TextComponent(displayName);
    }

    @Override
    public @Nullable NpcSkinData getSkinData() {
        return skinData;
    }
}
