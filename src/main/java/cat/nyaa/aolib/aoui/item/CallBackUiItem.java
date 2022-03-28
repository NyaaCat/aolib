package cat.nyaa.aolib.aoui.item;

import cat.nyaa.aolib.aoui.data.WindowClickData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class CallBackUiItem implements IClickableUiItem, IUiItem {
    private final BiConsumer<WindowClickData, Player> onClickConsumer;
    private final Function<Player, ItemStack> windowItemFunction;

    public CallBackUiItem(BiConsumer<WindowClickData, Player> onClick, Function<Player, ItemStack> windowItem) {
        this.onClickConsumer = onClick;
        this.windowItemFunction = windowItem;
    }


    @Override
    public ItemStack getWindowItem(Player player) {
        return windowItemFunction.apply(player);
    }

    @Override
    public void onClick(WindowClickData clickData, Player player) {
        onClickConsumer.accept(clickData, player);
    }
}
