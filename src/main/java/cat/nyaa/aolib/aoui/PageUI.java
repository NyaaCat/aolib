package cat.nyaa.aolib.aoui;

import cat.nyaa.aolib.AoLibPlugin;
import cat.nyaa.aolib.aoui.item.*;
import cat.nyaa.aolib.network.data.DataClickType;
import cat.nyaa.nyaacore.LanguageRepository;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PageUI extends BaseUI {
    public static final int PAGE_SIZE = 45;
    private static final int PLAYER_INVENTORY = 53;
    protected int page = 1;
    List<IUiItem> activeUiItemList = new LinkedList<>();
    protected List<IUiItem> UiItems;
    protected UIManager uiManager;
    private final LanguageRepository i18n;

    public PageUI(List<IUiItem> uiItems) {
        this(uiItems, AoLibPlugin.getI18n().orElse(null));
    }

    public PageUI(List<IUiItem> uiItems, LanguageRepository i18n) {
        this.i18n = i18n;
        this.UiItems = uiItems;
        this.fillEmpty(9);
        this.addButton();
        addPlayerInventoryItem();
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public int getUiItemsSize() {
        return UiItems.size();
    }

    public void addButton() {
        var buttonPrev = new ButtonUIItem(-1, this, i18n);
        var buttonNext = new ButtonUIItem(1, this, i18n);
        activeUiItemList.set(45, buttonPrev);
        activeUiItemList.set(53, buttonNext);

    }

    protected boolean check(int slotNum, DataClickType clickType, Player player) {
        if (!clickType.equals(DataClickType.PICKUP)) {
            return true;
        }
        // Player Inventory
        if (slotNum > PLAYER_INVENTORY) {
            return true;
        }
        if (activeUiItemList.get(slotNum) == EmptyUIItem.EMPTY_UI_ITEM) {
            return true;
        }
        if (activeUiItemList.get(slotNum) instanceof ButtonUIItem) {
            ((ButtonUIItem) activeUiItemList.get(slotNum)).onClick(clickType, player);
            return true;
        }
        return false;
    }

    public void fillEmpty(int cols) {
        for (int j = 0; j < 6; ++j) {
            for (int k = 0; k < cols; ++k) {
                activeUiItemList.add(EmptyUIItem.EMPTY_UI_ITEM);
            }
        }
    }

    public void applyUIItems() {
        this.fillEmpty(8);
        for (int i = 0; i < PAGE_SIZE; i++) {
            int itemIndex = i + (page - 1) * PAGE_SIZE;
            if (itemIndex >= UiItems.size()) {
                break;
            }

            var item = UiItems.get(itemIndex);
            activeUiItemList.set(i, item);
        }
        uiManager.broadcastChanges(this);
    }


    protected void addPlayerInventoryItem() {
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                activeUiItemList.add(new PlayerInventoryItem(j1 + l * 9 + 9));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            activeUiItemList.add(new PlayerInventoryItem(i1));
        }
    }


    @Override
    public @NotNull List<ItemStack> getWindowItem(Player player) {
        return activeUiItemList.stream().map(uiItem -> uiItem.getWindowItem(player)).collect(Collectors.toList());
    }

    @Override
    public void onWindowClick(int slotNum, int buttonNum, DataClickType clickType, Player player) {
        var item = activeUiItemList.get(slotNum);
        if (item instanceof IClickableUiItem) {
            ((IClickableUiItem) item).onClick(this, this.uiManager, slotNum, buttonNum, clickType, player);
        }
    }
}
