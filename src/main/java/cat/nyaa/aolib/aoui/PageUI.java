package cat.nyaa.aolib.aoui;

import cat.nyaa.aolib.AoLibPlugin;
import cat.nyaa.aolib.aoui.item.CallBackUiItem;
import cat.nyaa.aolib.aoui.item.EmptyUIItem;
import cat.nyaa.aolib.aoui.item.IClickableUiItem;
import cat.nyaa.aolib.aoui.item.IUiItem;
import cat.nyaa.aolib.network.data.DataClickType;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class PageUI extends BaseUI {
    private final List<IUiItem> allUiItem;
    private final List<IUiItem> buttonItem;
    private final Consumer<IBaseUI> updateConsumer;
    int page = 0;
    @NotNull
    private final String uiTitle;

    public PageUI(List<IUiItem> allUiItem, List<IUiItem> buttonItem, Consumer<IBaseUI> updateConsumer, @NotNull String uiTitle) {
        this.allUiItem = allUiItem;
        this.buttonItem = buttonItem;
        this.updateConsumer = updateConsumer;
        this.uiTitle = uiTitle;
    }

    public PageUI(List<IUiItem> allUiItem, Consumer<IBaseUI> updateConsumer, @NotNull String uiTitle) {
        this.allUiItem = allUiItem;
        this.buttonItem = getAllSimpleButtonUiItem(this);
        this.updateConsumer = updateConsumer;
        this.uiTitle = uiTitle;
    }

    public int getPage() {
        return page;
    }

    public static @NotNull List<IUiItem> getAllSimpleButtonUiItem(PageUI pageUI) {
        var uiItemArray = new IUiItem[9];
        Arrays.fill(uiItemArray, EmptyUIItem.EMPTY_UI_ITEM);
        uiItemArray[0] = getSimplePageButtonUiItem(pageUI, ButtonPageType.PREV);
        uiItemArray[8] = getSimplePageButtonUiItem(pageUI, ButtonPageType.NEXT);
        return Lists.newArrayList(uiItemArray);
    }

    @Contract(value = "_, _ -> new", pure = true)
    private static @NotNull CallBackUiItem getSimplePageButtonUiItem(PageUI pageUI, ButtonPageType type) {
        return new CallBackUiItem(
                ((clickType, player) -> {
                    if (clickType.equals(DataClickType.PICKUP)) {
                        switch (type) {
                            case NEXT -> {
                                if (pageUI.hasNextPage()) pageUI.nextPage();
                            }
                            case PREV -> {
                                if (pageUI.hasPrevPage()) pageUI.prevPage();
                            }
                        }
                    }
                }),
                ((player) -> {
                    if (type == ButtonPageType.PREV) {
                        if (!pageUI.hasPrevPage()) return UIPlayerHold.EMPTY_ITEM.clone();
                    }
                    if (type == ButtonPageType.NEXT) {
                        if (!pageUI.hasNextPage()) return UIPlayerHold.EMPTY_ITEM.clone();
                    }
                    var i18nOptional = AoLibPlugin.getI18n();
                    if (i18nOptional.isEmpty()) return new ItemStack(Material.ARROW);
                    var pluginI18n = i18nOptional.get();
                    var itemStack = new ItemStack(Material.ARROW);
                    var meta = itemStack.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(type == ButtonPageType.NEXT ? pluginI18n.getFormatted("ui.item.button.next") : pluginI18n.getFormatted("ui.item.button.last"));
                    }
                    itemStack.setItemMeta(meta);
                    return itemStack;
                })
        );
    }

    public void setPage(int page) {
        var maxPage = getMaxPage();
        if (page >= maxPage) {
            this.page = maxPage;
        } else this.page = Math.max(page, 0);
        updateConsumer.accept(this);
    }

    @Contract(pure = true)
    public boolean hasNextPage() {
        return page < getMaxPage();
    }

    public void nextPage() {
        setPage(getPage() + 1);
    }

    @Contract(pure = true)
    public boolean hasPrevPage() {
        return page > 0;
    }

    public void prevPage() {
        setPage(getPage() - 1);
    }

    @Contract(pure = true)
    public int getMaxPage() {
        return Math.floorDiv(allUiItem.size(), (4 * 9));
    }

    @Override
    public @NotNull BaseComponent getTitle() {
        return new TextComponent(this.uiTitle);
    }

    private @NotNull List<IUiItem> getPageUiItem() {
        List<IUiItem> result = new ArrayList<>();
        int start = (page) * (5 * 9);
        int end = (page + 1) * (5 * 9);
        for (int i = start; i < end; i++) {
            if (i < allUiItem.size()) {
                result.add(allUiItem.get(i));
            } else {
                result.add(EmptyUIItem.EMPTY_UI_ITEM);
            }
        }
        for (int i = 0; i < 9; i++) {
            if (i < buttonItem.size()) {
                result.add(buttonItem.get(i));
            } else {
                result.add(EmptyUIItem.EMPTY_UI_ITEM);
            }
        }
        return result;
    }

    @Override
    public @NotNull List<ItemStack> getWindowItem(Player player) {
        return getPageUiItemAll().stream().map(iUiItem -> iUiItem.getWindowItem(player)).toList();
    }

    List<IUiItem> getPageUiItemAll() {
        List<IUiItem> result = Lists.newArrayList();
        var pageUiItem = getPageUiItem();
        for (int i = 0; i < uiItemList.size(); i++) {
            if (i < pageUiItem.size()) {
                result.add(pageUiItem.get(i));
            } else {
                result.add(uiItemList.get(i));
            }
        }
        return result;
    }

    @Override
    public void onWindowClick(int slotNum, int buttonNum, DataClickType clickType, Player player) {
        var pageItemAll = getPageUiItemAll();
        if (slotNum < pageItemAll.size() && slotNum >= 0) {
            var uiItem = pageItemAll.get(slotNum);
            if (uiItem instanceof IClickableUiItem) {
                ((IClickableUiItem) uiItem).onClick(slotNum, buttonNum, clickType, player);
            }
        }
    }

    public enum ButtonPageType {
        PREV, NEXT
    }

}
