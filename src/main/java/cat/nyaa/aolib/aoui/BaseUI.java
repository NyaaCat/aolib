package cat.nyaa.aolib.aoui;

import cat.nyaa.aolib.aoui.item.EmptyUIItem;
import cat.nyaa.aolib.aoui.item.IUiItem;
import cat.nyaa.aolib.aoui.item.PlayerInventoryItem;
import cat.nyaa.aolib.network.data.DataClickType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class BaseUI implements IBaseUI {
    public static final Logger logger = Logger.getLogger("AOUI_BASE");
    private static final int WINDOW_ID = 112; // <127

    List<IUiItem> uiItemList = new ArrayList<>();

    public BaseUI() {
        for (int j = 0; j < 6; ++j) {
            for (int k = 0; k < 9; ++k) {
                uiItemList.add(EmptyUIItem.EMPTY_UI_ITEM);
            }
        }
        addPlayerInventoryItem();
    }

    protected void addPlayerInventoryItem() {
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                uiItemList.add(new PlayerInventoryItem(j1 + l * 9 + 9));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            uiItemList.add(new PlayerInventoryItem(i1));
        }
    }

    @Override
    public void onWindowClose() {

    }

    @Override
    public int getWindowId() {
        return WINDOW_ID;
    }

    @Override
    public int getTypeId() {
        return 5;
    }//GENERIC_9x6

    @Override
    public @NotNull BaseComponent getTitle() {
        return new TextComponent("233");
    }

    @Override
    public @NotNull List<ItemStack> getWindowItem(Player player) {
        return uiItemList.stream().map(uiItem -> uiItem.getWindowItem(player)).collect(Collectors.toList());
    }

    @Override
    public @NotNull ItemStack getCarriedWindowItem() {
        return new ItemStack(Material.AIR);
    }

    @Override
    public int getSlotSize() {
        return 6 * 9 + 3 * 9 + 9;//GENERIC_9x6
    }

    @Override
    public void onButtonClick(int buttonId, Player player) {
    }

    @Override
    public void onWindowClick(int slotNum, int buttonNum, DataClickType clickType, Player player) {
        logger.info(String.valueOf(slotNum));
        logger.info(String.valueOf(buttonNum));
        logger.info(clickType.name());
    }

    @Override
    public int getDataSize() {
        return 0;
    }

    @Override
    public int[] getWindowData(Player player) {
        return new int[0];
    }
}
