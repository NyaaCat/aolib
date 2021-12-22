package cat.nyaa.aolib.aoui;


import cat.nyaa.aolib.UISynchronizer;
import cat.nyaa.aolib.aoui.network.PacketListener;
import cat.nyaa.aolib.network.packet.game.*;
import com.comphenix.protocol.wrappers.ComponentConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.comphenix.protocol.ProtocolLibrary.getProtocolManager;

public class UIManager {
    private final Map<UUID, UIPlayerHold> playerUI = new HashMap<>();
    private final Plugin plugin;
    private final PacketListener packetListener;
    private final UIListener eventListener;
    private final UISynchronizer uiSynchronizer;

    public UIManager(Plugin plugin) {
        this.plugin = plugin;
        this.packetListener = new PacketListener(this.plugin, this);
        this.eventListener = new UIListener(this.plugin, this);
        this.uiSynchronizer = new UISynchronizer() {
            @Override
            public void sendInitialData(UIPlayerHold uiPlayerHold, List<ItemStack> items, ItemStack carriedItem, int[] data) {
                try {
                    new WrappedClientboundContainerSetContentPacket(uiPlayerHold.getWindowId(), uiPlayerHold.incrementStateId(), items, carriedItem).sendServerPacket(uiPlayerHold.getPlayer());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < data.length; ++i) {
                    this.broadcastDataValue(uiPlayerHold, i, data[i]);
                }

            }

            @Override
            public void sendSlotChange(UIPlayerHold uiPlayerHold, int slot, ItemStack itemStack) {
                try {
                    new WrappedClientboundContainerSetSlotPacket(uiPlayerHold.getWindowId(), uiPlayerHold.incrementStateId(), slot, itemStack).sendServerPacket(uiPlayerHold.getPlayer());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void sendCarriedChange(UIPlayerHold uiPlayerHold, ItemStack itemStack) {
                try {
                    new WrappedClientboundContainerSetSlotPacket(-1, uiPlayerHold.incrementStateId(), -1, itemStack).sendServerPacket(uiPlayerHold.getPlayer());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void sendDataChange(UIPlayerHold uiPlayerHold, int id, int value) {
                broadcastDataValue(uiPlayerHold, id, value);
            }

            private void broadcastDataValue(UIPlayerHold uiPlayerHold, int id, int value) {
                try {
                    new WrappedClientboundContainerSetDataPacket(uiPlayerHold.getWindowId(), id, value).sendServerPacket(uiPlayerHold.getPlayer());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }

        };
        plugin.getServer().getPluginManager().registerEvents(eventListener, this.plugin);
        getProtocolManager().addPacketListener(packetListener);
    }

    public void destructor() {
        getProtocolManager().removePacketListener(packetListener);
        HandlerList.unregisterAll(eventListener);
    }

    protected void flashPlayerUIList() {
        List<UUID> willBeRemoved = new ArrayList<>();
        for (UUID uuid : playerUI.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                willBeRemoved.add(uuid);
                continue;
            }

            if (!player.isOnline()) {
                willBeRemoved.add(uuid);
                continue;
            }

            if (!player.isValid()) {
                willBeRemoved.add(uuid);
            }

        }
        willBeRemoved.forEach(this::closeWindow);
    }

    public void sendCloseWindow(Player player) {
        UUID playerId = player.getUniqueId();
        if (playerUI.containsKey(playerId)) {
            try {
                new WrappedClientboundContainerClosePacket(playerUI.get(playerId).getWindowId()).sendServerPacket(player);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            closeWindow(playerId);
        }
    }

    public void sendOpenWindow(Player player, IBaseUI ui) {
        UUID playerId = player.getUniqueId();
        if (playerUI.containsKey(playerId)) {
            sendCloseWindow(player);
        }
        try {
            new WrappedClientboundOpenScreenPacket(ui.getWindowId(), ui.getTypeId(), ComponentConverter.fromBaseComponent(ui.getTitle())).sendServerPacket(player);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        openWindow(player, ui);
    }

//    public boolean sendAllData(Player player) {
//        UUID playerId = player.getUniqueId();
//        if (!playerUI.containsKey(playerId)) {
//            return false;
//        }
//        UIPlayerHold ui = playerUI.get(playerId);
//        ui.broadcastFullState();
//        //uiSynchronizer.sendInitialData(ui, ui.getHoldUI().getWindowItem(),ui.getHoldUI().getCarriedWindowItem(),ui.getHoldUI().getWindowData());
//        return true;
//    }


    protected void closeWindow(UUID playerId) {
        if (playerUI.containsKey(playerId)) {
            playerUI.get(playerId).getHoldUI().onWindowClose();
            playerUI.remove(playerId);
        }
    }

    protected void openWindow(Player player, IBaseUI ui) {
        UUID playerId = player.getUniqueId();
        if (playerUI.containsKey(playerId)) {
            closeWindow(playerId);
        }
        playerUI.put(playerId, new UIPlayerHold(ui, player, this.uiSynchronizer));
        //sendAllData
    }

    protected List<Player> getPlayerListByUi(IBaseUI ui) {
        List<Player> result = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            UUID playerId = player.getUniqueId();
            if (playerUI.containsKey(playerId)) {
                if (playerUI.get(playerId).getHoldUI().equals(ui))
                    result.add(player);
            }
        });
        return result;
    }

    public void broadcastChanges(IBaseUI ui) {
        getPlayerListByUi(ui).forEach(this::broadcastChanges);
    }

    public void broadcastChanges(Player player) {
        UUID playerId = player.getUniqueId();
        if (!playerUI.containsKey(playerId)) return;
        UIPlayerHold uiPlayerHold = playerUI.get(playerId);
        broadcastChanges(uiPlayerHold);
    }

    public void broadcastChanges(UIPlayerHold uiPlayerHold) {
        uiPlayerHold.broadcastChanges();
    }

    public void handlePlayerQuit(Player player) {
        closeWindow(player.getUniqueId());
        flashPlayerUIList();
    }


    public void handleWindowClick(Player player, WrappedServerboundContainerClickPacket wrappedPacket) {
        UUID playerId = player.getUniqueId();
        if (!playerUI.containsKey(playerId)) return;
        UIPlayerHold uiPlayerHold = playerUI.get(playerId);
        plugin.getLogger().info(String.valueOf(wrappedPacket.getContainerId()));
        if (uiPlayerHold.getWindowId() != wrappedPacket.getContainerId()) return;
        boolean flag = wrappedPacket.getStateId() != uiPlayerHold.getStateId();
        uiPlayerHold.suppressRemoteUpdates();

        uiPlayerHold.getHoldUI().onWindowClick(wrappedPacket.getSlotNum(), wrappedPacket.getButtonNum(), wrappedPacket.getClickType(), player);

        wrappedPacket.getChangedSlots().forEach(uiPlayerHold::setRemoteSlotNoCopy);

        uiPlayerHold.setRemoteCarried(wrappedPacket.getCarriedItem());
        uiPlayerHold.resumeRemoteUpdates();
        if (flag) {
            uiPlayerHold.broadcastFullState();
        } else {
            uiPlayerHold.broadcastChanges();
        }
    }

    public void handleWindowButtonClick(Player player, WrappedServerboundContainerButtonClickPacket wrappedPacket) {
        UUID playerId = player.getUniqueId();
        if (!playerUI.containsKey(playerId)) return;
        if (playerUI.get(playerId).getWindowId() != wrappedPacket.getContainerId()) return;
        playerUI.get(playerId).getHoldUI().onButtonClick(wrappedPacket.getButtonId(),player);
    }

    public void handleWindowClose(Player player, WrappedServerboundContainerClosePacket wrappedPacket) {
        UUID playerId = player.getUniqueId();
        if (!playerUI.containsKey(playerId)) return;
        if (playerUI.get(playerId).getWindowId() == wrappedPacket.getContainerId()) {
            closeWindow(playerId);
        } else {
            sendCloseWindow(player);
        }
    }
}

