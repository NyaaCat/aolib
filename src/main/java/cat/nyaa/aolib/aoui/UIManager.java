package cat.nyaa.aolib.aoui;


import cat.nyaa.aolib.UISynchronizer;
import cat.nyaa.aolib.aoui.data.WindowClickData;
import cat.nyaa.aolib.aoui.network.UIPacketListener;
import cat.nyaa.aolib.network.packet.game.*;
import cat.nyaa.aolib.utils.TaskUtils;
import com.comphenix.protocol.wrappers.ComponentConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.comphenix.protocol.ProtocolLibrary.getProtocolManager;

public class UIManager {
    private final ConcurrentHashMap<UUID, UIPlayerHold> playerUI = new ConcurrentHashMap<>();
    private final Plugin plugin;
    private final UIPacketListener packetListener;
    private final UIListener eventListener;
    private final UISynchronizer uiSynchronizer;

    public UIManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.packetListener = new UIPacketListener(this.plugin, this);
        this.eventListener = new UIListener(this.plugin, this);
        this.uiSynchronizer = new UISynchronizer() {
            @Override
            public void sendInitialData(@NotNull UIPlayerHold uiPlayerHold, @NotNull List<ItemStack> items, @NotNull ItemStack carriedItem, int[] data) {
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
            public void sendSlotChange(@NotNull UIPlayerHold uiPlayerHold, int slot, @NotNull ItemStack itemStack) {
                try {
                    new WrappedClientboundContainerSetSlotPacket(uiPlayerHold.getWindowId(), uiPlayerHold.incrementStateId(), slot, itemStack).sendServerPacket(uiPlayerHold.getPlayer());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void sendCarriedChange(@NotNull UIPlayerHold uiPlayerHold, @NotNull ItemStack itemStack) {
                try {
                    new WrappedClientboundContainerSetSlotPacket(-1, uiPlayerHold.incrementStateId(), -1, itemStack).sendServerPacket(uiPlayerHold.getPlayer());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void sendDataChange(@NotNull UIPlayerHold uiPlayerHold, int id, int value) {
                broadcastDataValue(uiPlayerHold, id, value);
            }

            private void broadcastDataValue(@NotNull UIPlayerHold uiPlayerHold, int id, int value) {
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

    public Plugin getPlugin() {
        return plugin;
    }

    public void destructor() {
        try {
            flashPlayerUIList();
            this.playerUI.keySet().forEach(uuid -> {
                var player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    this.sendCloseWindow(player);
                }
            });
            this.playerUI.keySet().forEach(this::closeWindow);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void sendCloseWindow(@NotNull UUID playerId) {
        var player = Bukkit.getPlayer(playerId);
        if (player == null) return;
        sendCloseWindow(player);
    }

    public void sendCloseWindow(@NotNull Player player) {
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

    public void sendOpenWindow(@NotNull Player player, @NotNull IBaseUI ui) {
        UUID playerId = player.getUniqueId();
        if (playerUI.containsKey(playerId)) {
            sendCloseWindow(player);
        }
        try {
            new WrappedClientboundOpenScreenPacket(ui.getWindowId(), ui.getTypeId(), ComponentConverter.fromBaseComponent(ui.getTitle(player))).sendServerPacket(player);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (playerUI.containsKey(playerId)) {
            closeWindow(playerId);
        }
        playerUI.put(playerId, new UIPlayerHold(ui, player, this.uiSynchronizer));
        ui.onWindowOpen(player);
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


    protected void closeWindow(@NotNull UUID playerId) {
        if (playerUI.containsKey(playerId)) {
            playerUI.get(playerId).getHoldUI().onWindowClose();
            playerUI.remove(playerId);
        }
    }

    protected List<Player> getPlayerListByUi(@NotNull IBaseUI ui) {
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

    public void broadcastChanges(@NotNull IBaseUI ui) {
        getPlayerListByUi(ui).forEach(this::broadcastChanges);
    }

    public void broadcastFullState(@NotNull IBaseUI ui) {
        getPlayerListByUi(ui).forEach(this::broadcastFullState);
    }


    public void broadcastChanges(@NotNull Player player) {
        getUiPlayerHoldToRun(player, this::broadcastChanges);
    }

    public void broadcastFullState(@NotNull Player player) {
        getUiPlayerHoldToRun(player, this::broadcastFullState);
    }

    private void getUiPlayerHoldToRun(@NotNull Player player, @NotNull Consumer<UIPlayerHold> consumer) {
        UUID playerId = player.getUniqueId();
        if (!playerUI.containsKey(playerId)) return;
        UIPlayerHold uiPlayerHold = playerUI.get(playerId);
        consumer.accept(uiPlayerHold);
    }


    public void broadcastChanges(@NotNull UIPlayerHold uiPlayerHold) {
        uiPlayerHold.broadcastChanges();
    }

    public void broadcastFullState(@NotNull UIPlayerHold uiPlayerHold) {
        uiPlayerHold.broadcastFullState();
    }

    public void handlePlayerQuit(@NotNull Player player) {
        closeWindow(player.getUniqueId());
        flashPlayerUIList();
    }

    private boolean hasUiAsync(@NotNull UUID playerId, int windowId) {
        if (!playerUI.containsKey(playerId)) return false;
        return playerUI.get(playerId).getWindowId() == windowId;
    }


    public boolean handleWindowClickAsync(@NotNull UUID playerId, @NotNull WrappedServerboundContainerClickPacket wrappedPacket) {
        if (!hasUiAsync(playerId, wrappedPacket.getContainerId())) return false;
        TaskUtils.async.callSync(() -> handleWindowClick(playerId, wrappedPacket));
        return true;
    }

    private void handleWindowClick(@NotNull UUID playerId, @NotNull WrappedServerboundContainerClickPacket wrappedPacket) {
        var player = Bukkit.getPlayer(playerId);
        if (player == null) return;
        var windowClickData = new WindowClickData(wrappedPacket);
        UIPlayerHold uiPlayerHold = playerUI.get(playerId);
        boolean flag = windowClickData.stateId() != uiPlayerHold.getStateId();
        uiPlayerHold.suppressRemoteUpdates();
        var carriedItem = windowClickData.carriedItem();
        uiPlayerHold.getHoldUI().onWindowClick(windowClickData, player);

        windowClickData.changedSlots().forEach(uiPlayerHold::setRemoteSlotNoCopy);
        uiPlayerHold.setRemoteCarried(carriedItem);
        uiPlayerHold.resumeRemoteUpdates();
        if (flag) {
            uiPlayerHold.broadcastFullState();
        } else {
            uiPlayerHold.broadcastChanges();
        }
    }


    public boolean handleWindowButtonClickAsync(@NotNull UUID playerId, @NotNull WrappedServerboundContainerButtonClickPacket wrappedPacket) {
        if (!hasUiAsync(playerId, wrappedPacket.getContainerId())) return false;
        TaskUtils.async.callSync(() -> handleWindowButtonClick(playerId, wrappedPacket.getButtonId()));
        return true;
    }

    private void handleWindowButtonClick(@NotNull UUID playerId, int buttonId) {
        var player = Bukkit.getPlayer(playerId);
        if (player == null) return;
        playerUI.get(playerId).getHoldUI().onButtonClick(buttonId, player);

    }

    public boolean handleWindowCloseAsync(@NotNull UUID playerId, @NotNull WrappedServerboundContainerClosePacket wrappedPacket) {
        if (!playerUI.containsKey(playerId)) return false;
        if (playerUI.get(playerId).getWindowId() == wrappedPacket.getContainerId()) {
            TaskUtils.async.callSync(() -> closeWindow(playerId));
            return true;
        } else {
            TaskUtils.async.callSync(() -> sendCloseWindow(playerId));
        }
        return false;
    }
}


