package cat.nyaa.aolib.aoui.item;

import cat.nyaa.aolib.network.data.DataClickType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class CallBackUiItem implements IClickableUiItem, IUiItem {
    private final BiConsumer<DataClickType, Player> onClickConsumer;
    private final Function<Player, ItemStack> windowItemFunction;

    public CallBackUiItem(BiConsumer<DataClickType, Player> onClick, Function<Player, ItemStack> windowItem) {
        this.onClickConsumer = onClick;
        this.windowItemFunction = windowItem;
    }

    @Override
    public void onClick(DataClickType clickType, Player player) {
        onClickConsumer.accept(clickType, player);
    }

    @Override
    public ItemStack getWindowItem(Player player) {
        return windowItemFunction.apply(player);
    }
}
