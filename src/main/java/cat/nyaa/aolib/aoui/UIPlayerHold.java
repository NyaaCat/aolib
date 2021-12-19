package cat.nyaa.aolib.aoui;

import cat.nyaa.aolib.UISynchronizer;
import com.google.common.base.Suppliers;
import com.google.common.primitives.Ints;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UIPlayerHold {

    public final static ItemStack EMPTY_ITEM = new ItemStack(Material.AIR);
    private final IBaseUI holdUI;
    private final Player player;
    private final List<ItemStack> remoteSlots;
    private final List<Integer> remoteDataSlots;
    private ItemStack remoteCarried;
    private int stateId;
    private final UISynchronizer uiSynchronizer;
    private boolean suppressRemoteUpdates;

    public UIPlayerHold(IBaseUI holdUI, Player player, UISynchronizer uiSynchronizer) {
        this.holdUI = holdUI;
        this.player = player;
        this.uiSynchronizer = uiSynchronizer;
        remoteSlots = new ArrayList<>();
        while (remoteSlots.size() < getHoldUI().getSlotSize()) {
            remoteSlots.add(EMPTY_ITEM);
        }

        remoteDataSlots = new ArrayList<>();
        while (remoteDataSlots.size() < getHoldUI().getDataSize()) {
            remoteDataSlots.add(0);
        }
        remoteCarried = EMPTY_ITEM;
        this.sendAllDataToRemote();

    }

    public void setStateId(int id) {//setItem,initializeContents
        this.stateId = id;
    }

    public int getStateId() {
        return stateId;
    }

    public int incrementStateId() {
        this.stateId = this.stateId + 1 & 32767;
        return this.stateId;
    }

    public IBaseUI getHoldUI() {
        return holdUI;
    }

    public Player getPlayer() {
        return player;
    }

    public void setRemoteSlot(int slotId, ItemStack itemStack) {
        this.remoteSlots.set(slotId, itemStack.clone());
    }

    public void setRemoteSlotNoCopy(int slotId, ItemStack itemStack) {
        this.remoteSlots.set(slotId, itemStack);
    }

    public void broadcastFullState() {
        this.sendAllDataToRemote();
    }

    public void sendAllDataToRemote() {
        List<ItemStack> slots = getWindowItem();

        for (int i = 0; i < slots.size(); i++) {
            this.remoteSlots.set(i, slots.get(i).clone());
        }

        this.remoteCarried = this.getHoldUI().getCarriedWindowItem().clone();


        int[] data = getHoldUI().getWindowData();
        for (int i = 0; i < data.length; i++) {
            this.remoteDataSlots.set(i, data[i]);
        }


        if (this.uiSynchronizer != null) {
            this.uiSynchronizer.sendInitialData(this, this.remoteSlots, this.remoteCarried, Ints.toArray(this.remoteDataSlots));
        }

    }

    public void broadcastChanges() {
        List<ItemStack> slots = getWindowItem();
        for (int i = 0; i < slots.size(); ++i) {
            ItemStack itemstack = slots.get(i);
            Supplier<ItemStack> supplier = Suppliers.memoize(itemstack::clone);
            this.synchronizeSlotToRemote(i, itemstack, supplier);
        }
        this.synchronizeCarriedToRemote();

        int[] data = getHoldUI().getWindowData();
        for (int i = 0; i < data.length; i++) {
            int j = data[i];
            this.synchronizeDataSlotToRemote(i, j);
        }
    }

    private void synchronizeSlotToRemote(int slotId, ItemStack itemStack, Supplier<ItemStack> stackClone) {
        if (!this.suppressRemoteUpdates) {
            ItemStack remoteItemStack = this.remoteSlots.get(slotId);
            if (!remoteItemStack.equals(itemStack)) {
                ItemStack itemstack1 = stackClone.get();
                this.remoteSlots.set(slotId, itemstack1);
                if (this.uiSynchronizer != null) {
                    this.uiSynchronizer.sendSlotChange(this, slotId, itemstack1);
                }
            }
        }
    }

    private void synchronizeCarriedToRemote() {
        if (!this.suppressRemoteUpdates) {
            if (!this.remoteCarried.equals(this.getHoldUI().getCarriedWindowItem())) {
                this.remoteCarried = this.getHoldUI().getCarriedWindowItem().clone();
                if (this.uiSynchronizer != null) {
                    this.uiSynchronizer.sendCarriedChange(this, this.remoteCarried);
                }
            }

        }
    }

    private void synchronizeDataSlotToRemote(int dataIndex, int value) {
        if (!this.suppressRemoteUpdates) {
            int remoteValue = this.remoteDataSlots.get(dataIndex);
            if (remoteValue != value) {
                this.remoteDataSlots.set(dataIndex, value);
                if (this.uiSynchronizer != null) {
                    this.uiSynchronizer.sendDataChange(this, dataIndex, value);
                }
            }
        }
    }

    public List<ItemStack> getWindowItem() {
        List<ItemStack> slots = getHoldUI().getWindowItem();
        int slotSize = getHoldUI().getSlotSize();
        slots = slots.subList(0, Math.min(slotSize, slots.size()));
        while (slots.size() < slotSize) {
            slots.add(EMPTY_ITEM);
        }
        return slots;
    }

    public int getWindowId() {
        return getHoldUI().getWindowId();
    }

    public List<Integer> getWindowDataList() {
        int[] dataArray = getHoldUI().getWindowData();
        int dataSize = getHoldUI().getDataSize();
        return getWindowDataList(dataArray, dataSize);
    }

    public List<Integer> getWindowDataList(int[] dataArray, int dataSize) {

        List<Integer> dataList = new ArrayList<>(dataArray.length);
        for (int i : dataArray) {
            dataList.add(i);
        }

        dataList = dataList.subList(0, Math.min(dataSize, dataList.size()));
        while (dataList.size() < dataSize) {
            dataList.add(0);
        }

        return dataList;
    }

    public int[] getWindowData() {
        int[] dataArray = getHoldUI().getWindowData();
        int dataSize = getHoldUI().getDataSize();
        if (dataArray.length == dataSize) return dataArray;
        return Ints.toArray(getWindowDataList(dataArray, dataSize));
    }
}
