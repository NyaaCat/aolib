package cat.nyaa.aolib.npc;

import cat.nyaa.aolib.network.packet.game.WrappedClientboundPlayerInfoPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundRemoveEntitiesPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundSetEntityDataPacket;
import cat.nyaa.aolib.network.packet.game.WrappedClientboundSetEquipmentPacket;
import cat.nyaa.aolib.npc.data.NpcEntityData;
import cat.nyaa.aolib.npc.data.NpcEquipmentData;
import cat.nyaa.aolib.utils.TaskUtils;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class NpcManager {
    Map<String, TrackedNpc> TrackedNpcMap = new HashMap<>();
    NpcPlayerMap npcPlayerMap = new NpcPlayerMap();
    private long tickNum = 0;

    // todo int LoadingBoundary

    public void handleTick() {
        var activeNameSet = npcPlayerMap.getActiveNpcNameSet();
        TrackedNpcMap.forEach((s, trackedNpc) -> {
                    if (activeNameSet.contains(s)) {
                        trackedNpc.getAoNpc().onNpcTick(tickNum, true);
                        trackedNpc.sendChanges();
                    } else {
                        TaskUtils.mod128TickToRun(tickNum, trackedNpc.getAoNpc().getUUID(), () -> {
                            trackedNpc.getAoNpc().onNpcTick(tickNum, false);
                            trackedNpc.sendChanges();
                        });
                    }
                }
        );
        this.tickNum++;
    }

    public NpcPlayerMap getNpcPlayerMap() {
        return npcPlayerMap;
    }

    public boolean loadNpc(IAoEntityNpc npc) {
        if (!isNpcLoaded(npc)) {
            npc.onLoad();
            TrackedNpcMap.put(npc.getUniqueNpcName(), new TrackedNpc(npc.getUpdateInterval(), this, npc));
            return true;
        }
        return false;
    }

    public boolean unLoadNpc(IAoEntityNpc npc) {
        if (isNpcLoaded(npc)) {
            npc.onUnload();
            TrackedNpcMap.remove(npc.getUniqueNpcName());
            return true;
        }
        return false;
    }

    public boolean isNpcLoaded(@NotNull IAoEntityNpc npc) {
        return TrackedNpcMap.containsKey(npc.getUniqueNpcName());
    }

    public void sendAddNpc(Player target, @NotNull IAoEntityNpc npc) {
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
    private List<Pair<EnumWrappers.ItemSlot, ItemStack>> getLivingEntityEquipmentSlots(@NotNull IAoLivingEntityNpc livingEntityNpc) {
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

    public void sendRemoveNpc(Player target, @NotNull IAoEntityNpc npc) {
        npc.preSendRemove(target, npc);
        try {
            new WrappedClientboundRemoveEntitiesPacket(npc.getEntityId()).sendServerPacket(target);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Contract("_ -> new")
    private @NotNull PlayerInfoData getNpcPlayerInfoData(IAoPlayerNpc playerNpc) {
        return new PlayerInfoData(
                getPlayerNpcGameProfile(playerNpc),
                playerNpc.getLatency(),
                EnumWrappers.NativeGameMode.fromBukkit(playerNpc.getGameMode()),
                ComponentConverter.fromBaseComponent(playerNpc.getDisplayName())
        );
    }

    private @NotNull WrappedGameProfile getPlayerNpcGameProfile(@NotNull IAoPlayerNpc playerNpc) {
        var result = new WrappedGameProfile(playerNpc.getUUID(), playerNpc.getName());
        playerNpc.getNpcPlayerPropertyDataList().stream()
                .filter(Objects::nonNull)
                .map(npcPlayerPropertyData -> new WrappedSignedProperty(npcPlayerPropertyData.name(), npcPlayerPropertyData.value(), npcPlayerPropertyData.signature()))
                .forEach(property -> result.getProperties().put(property.getName(), property));
        return result;
    }

    public void onNpcTeleport(TrackedNpc trackedNpc) {
    }

    public void onNpcMove(TrackedNpc trackedNpc, boolean pos, boolean rot) {
    }

    public void destructor() {
        //todo
    }
}
