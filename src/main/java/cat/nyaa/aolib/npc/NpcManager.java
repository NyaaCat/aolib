package cat.nyaa.aolib.npc;

import cat.nyaa.aolib.network.packet.game.WrappedClientboundPlayerInfoPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundRemoveEntitiesPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundSetEntityDataPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundSetEquipmentPacket;
import cat.nyaa.aolib.npc.data.NpcEntityData;
import cat.nyaa.aolib.npc.data.NpcEquipmentData;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class NpcManager {
    Map<String, IAoEntityNpc> LoadedNpcSet = new HashMap<>();
    // todo int LoadingBoundary

    public boolean loadNpc(IAoEntityNpc npc) {
        if (!isNpcLoaded(npc)) {
            npc.onLoad();
            LoadedNpcSet.put(npc.getUniqueNpcName(), npc);
            return true;
        }
        return false;
    }

    public boolean unLoadNpc(IAoEntityNpc npc) {
        if (isNpcLoaded(npc)) {
            npc.onUnload();
            LoadedNpcSet.remove(npc.getUniqueNpcName());
            return true;
        }
        return false;
    }

    public boolean isNpcLoaded(IAoEntityNpc npc) {
        return LoadedNpcSet.containsKey(npc.getUniqueNpcName());
    }

    public void sendAddNpc(Player target, IAoEntityNpc npc) {
        npc.preSendAdd(target, npc);
        try {
            if (npc instanceof IAoPlayerNpc)
                new WrappedClientboundPlayerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER, getNpcPlayerInfoData((IAoPlayerNpc) npc)).sendServerPacket(target);

            npc.getAddEntityPacket().sendServerPacket(target);
            var npcWrappedWatchableObjectList = getNpcWrappedWatchableObjectList(npc);
            if (!npcWrappedWatchableObjectList.isEmpty())
                new WrappedClientboundSetEntityDataPacket(npc.getEntityId(), npcWrappedWatchableObjectList).sendServerPacket(target);

            if (npc instanceof IAoLivingEntityNpc)
                sendLivingEntityEquipmentSlots(target, (IAoLivingEntityNpc) npc);//todo
            if (npc instanceof IAoPlayerNpc)
                new WrappedClientboundPlayerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, getNpcPlayerInfoData((IAoPlayerNpc) npc)).sendServerPacket(target);

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void sendLivingEntityEquipmentSlots(Player target, IAoLivingEntityNpc livingEntityNpc) {
        var slots = getLivingEntityEquipmentSlots(livingEntityNpc);
        if (!slots.isEmpty()) {
            try {
                new WrappedClientboundSetEquipmentPacket(livingEntityNpc.getEntityId(), slots).sendServerPacket(target);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @NotNull
    private List<Pair<EnumWrappers.ItemSlot, ItemStack>> getLivingEntityEquipmentSlots(IAoLivingEntityNpc livingEntityNpc) {
        NpcEquipmentData npcEquipmentData = livingEntityNpc.getNpcEquipmentData();
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> result = new ArrayList<>();
        if (npcEquipmentData.mainHand() != null)
            result.add(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, npcEquipmentData.mainHand()));
        if (npcEquipmentData.offHand() != null)
            result.add(new Pair<>(EnumWrappers.ItemSlot.OFFHAND, npcEquipmentData.offHand()));
        if (npcEquipmentData.feet() != null)
            result.add(new Pair<>(EnumWrappers.ItemSlot.FEET, npcEquipmentData.feet()));
        if (npcEquipmentData.legs() != null)
            result.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, npcEquipmentData.legs()));
        if (npcEquipmentData.chest() != null)
            result.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, npcEquipmentData.chest()));
        if (npcEquipmentData.head() != null)
            result.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, npcEquipmentData.head()));
        /*
            ItemStack mainHand,
            ItemStack offHand,
            ItemStack feet,
            ItemStack legs,
            ItemStack chest,
            ItemStack head
        */
        return result;
    }

    @NotNull
    private List<WrappedWatchableObject> getNpcWrappedWatchableObjectList(@NotNull IAoEntityNpc npc) {
        var npcEntityDataList = npc.getNpcEntityDataList();
        if (npcEntityDataList == null || npcEntityDataList.isEmpty()) return new ArrayList<>();
        List<WrappedWatchableObject> list = new ArrayList<>();
        for (NpcEntityData npcEntityData : npcEntityDataList) {
            WrappedWatchableObject wrappedWatchableObject = new WrappedWatchableObject(
                    new WrappedDataWatcher.WrappedDataWatcherObject(
                            npcEntityData.dataId(),
                            WrappedDataWatcher.Registry.get(npcEntityData.type(), npcEntityData.optional())
                    ),
                    npcEntityData.value()
            );
            list.add(wrappedWatchableObject);
        }
        return list;
    }

    public void sendRemoveNpc(Player target, IAoEntityNpc npc) {
        npc.preSendRemove(target, npc);
        try {
            new WrappedClientboundRemoveEntitiesPacket(npc.getEntityId()).sendServerPacket(target);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private PlayerInfoData getNpcPlayerInfoData(IAoPlayerNpc playerNpc) {
        return new PlayerInfoData(
                getPlayerNpcGameProfile(playerNpc),
                playerNpc.getLatency(),
                EnumWrappers.NativeGameMode.fromBukkit(playerNpc.getGameMode()),
                ComponentConverter.fromBaseComponent(playerNpc.getDisplayName())
        );
    }

    private WrappedGameProfile getPlayerNpcGameProfile(IAoPlayerNpc playerNpc) {
        var result = new WrappedGameProfile(playerNpc.getUUID(), playerNpc.getName());
        playerNpc.getNpcPlayerPropertyDataList().stream()
                .filter(Objects::nonNull)
                .map(npcPlayerPropertyData -> new WrappedSignedProperty(npcPlayerPropertyData.name(), npcPlayerPropertyData.value(), npcPlayerPropertyData.signature()))
                .forEach(property -> result.getProperties().put(property.getName(), property));
        return result;
    }
}
