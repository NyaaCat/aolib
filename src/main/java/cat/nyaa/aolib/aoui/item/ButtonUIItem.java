package cat.nyaa.aolib.aoui.item;

import cat.nyaa.aolib.AoLibPlugin;
import cat.nyaa.aolib.aoui.PageUI;
import cat.nyaa.aolib.network.data.DataClickType;
import cat.nyaa.nyaacore.LanguageRepository;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

public class ButtonUIItem implements IClickableUiItem {
    private final int type;
    private final PageUI pageUI;
    private final LanguageRepository i18n;

    public ButtonUIItem(int type, PageUI pageUI, @Nullable LanguageRepository i18n) {
        this.type = type;
        this.pageUI = pageUI;
        this.i18n = Objects.requireNonNullElse(i18n, AoLibPlugin.getI18n().orElse(null));
    }


    @Override
    public void onClick(DataClickType clickType, Player player) {
        var newPage = this.pageUI.getPage() + type;
        if (newPage >= 1 && newPage <= this.pageUI.getUiItemsSize() * PageUI.PAGE_SIZE) {
            this.pageUI.setPage(newPage);
            this.pageUI.applyUIItems();
        }
    }

    @Override
    public ItemStack getWindowItem(Player player) {
        var itemStack = new ItemStack(Material.ARROW);
        var meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(this.type == 1 ? i18n.getFormatted("ui.item.button.next") : i18n.getFormatted("ui.item.button.last"));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
