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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PageUI extends BaseUI {
    @NotNull
    private final String uiTitle;
    ConcurrentHashMap<UUID, Integer> pageMap = new ConcurrentHashMap<>();
    private List<IUiItem> allUiItem;
    private List<IUiItem> buttonItem;
    private Consumer<IBaseUI> updateConsumer;

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

    @Override
    public void onWindowOpen(@NotNull Player player) {
        super.onWindowOpen(player);
        if (this.getPage(player.getUniqueId()) != 0) {
            this.setPage(player.getUniqueId(), 0);
        }
    }

    public static @NotNull List<IUiItem> getAllSimpleButtonUiItem(PageUI pageUI) {
        var uiItemArray = new IUiItem[9];
        Arrays.fill(uiItemArray, EmptyUIItem.EMPTY_UI_ITEM);
        uiItemArray[0] = getSimplePageButtonUiItem(pageUI, ButtonPageType.PREV);
        uiItemArray[8] = getSimplePageButtonUiItem(pageUI, ButtonPageType.NEXT);
        return Lists.newArrayList(uiItemArray);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull CallBackUiItem getSimplePageButtonUiItem(PageUI pageUI, ButtonPageType type) {
        return new CallBackUiItem(
                ((clickType, player) -> {
                    var playerUniqueId = player.getUniqueId();
                    if (clickType.equals(DataClickType.PICKUP)) {
                        switch (type) {
                            case NEXT -> {
                                if (pageUI.hasNextPage(playerUniqueId)) pageUI.nextPage(playerUniqueId);
                            }
                            case PREV -> {
                                if (pageUI.hasPrevPage(playerUniqueId)) pageUI.prevPage(playerUniqueId);
                            }
                        }
                    }
                }),
                ((player) -> {
                    var playerUniqueId = player.getUniqueId();
                    if (type == ButtonPageType.PREV) {
                        if (!pageUI.hasPrevPage(playerUniqueId)) return UIPlayerHold.EMPTY_ITEM.clone();
                    }
                    if (type == ButtonPageType.NEXT) {
                        if (!pageUI.hasNextPage(playerUniqueId)) return UIPlayerHold.EMPTY_ITEM.clone();
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

    public void setAllUiItem(List<IUiItem> allUiItem) {
        this.allUiItem = allUiItem;
        updateConsumer.accept(this);
    }

    public void setButtonItem(List<IUiItem> buttonItem) {
        this.buttonItem = buttonItem;
        updateConsumer.accept(this);
    }

    public void setUpdateConsumer(Consumer<IBaseUI> updateConsumer) {
        this.updateConsumer = updateConsumer;
    }

    public int getPage(UUID uuid) {
        return pageMap.getOrDefault(uuid, 0);
    }

    private void setPageNotUpdate(UUID uuid, int page) {
        var maxPage = getMaxPage();
        if (page >= maxPage) {
            this.pageMap.put(uuid, maxPage);

        } else this.pageMap.put(uuid, Math.max(page, 0));
    }
    public void setPage(UUID uuid, int page) {
        setPageNotUpdate(uuid, page);
        updateConsumer.accept(this);
    }

    @Contract(pure = true)
    public boolean hasNextPage(UUID uuid) {
        return getPage(uuid) < getMaxPage();
    }

    public void nextPage(UUID uuid) {
        setPage(uuid, getPage(uuid) + 1);
    }

    @Contract(pure = true)
    public boolean hasPrevPage(UUID uuid) {
        return getPage(uuid) > 0;
    }

    public void prevPage(UUID uuid) {
        setPage(uuid, getPage(uuid) - 1);
    }

    @Contract(pure = true)
    public int getMaxPage() {
        return Math.floorDiv(allUiItem.size(), (5 * 9));
    }

    @Override
    public @NotNull BaseComponent getTitle() {
        return new TextComponent(this.uiTitle);
    }

    private @NotNull List<IUiItem> getPageUiItem(Player player) {
        List<IUiItem> result = new ArrayList<>();
        var playerId = player.getUniqueId();
        int start = (getPage(playerId)) * (5 * 9);
        int end = (getPage(playerId) + 1) * (5 * 9);
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
        return getPageUiItemAll(player).stream().map(iUiItem -> iUiItem.getWindowItem(player)).toList();
    }

    List<IUiItem> getPageUiItemAll(Player player) {
        List<IUiItem> result = Lists.newArrayList();
        var pageUiItem = getPageUiItem(player);
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
        var pageItemAll = getPageUiItemAll(player);
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
