package cat.nyaa.aolib.aoui;

import cat.nyaa.aolib.AoLibPlugin;
import cat.nyaa.aolib.aoui.item.IUiItem;
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
    public @NotNull List<ItemStack> getWindowItem() {
        return uiItemList.stream().map(IUiItem::getWindowItem).collect(Collectors.toList());
    }

    @Override
    public @NotNull ItemStack getCarriedWindowItem() {
        return new ItemStack(Material.AIR);
    }

    @Override
    public void onButtonClick(int buttonId) {
    }

    @Override
    public void onWindowClick(int slotNum, int buttonNum, DataClickType clickType, Player player) {
        logger.info(String.valueOf(slotNum));
        logger.info(String.valueOf(buttonNum));
        logger.info(clickType.name());
    }

    @Override
    public int[] getWindowData() {
        return new int[0];
    }
}
