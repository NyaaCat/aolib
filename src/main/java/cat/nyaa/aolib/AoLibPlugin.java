package cat.nyaa.aolib;

import cat.nyaa.aolib.aoui.BaseUI;
import cat.nyaa.aolib.aoui.UIManager;
import cat.nyaa.aolib.npc.BasePlayerNpc;
import cat.nyaa.aolib.npc.NpcManager;
import cat.nyaa.aolib.utils.EntityDataUtils;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class AoLibPlugin extends JavaPlugin {
    private NpcManager npcManager;

    @Override
    public void onEnable() {
        this.npcManager = new NpcManager();
        EntityDataUtils.init();
        // Plugin startup logic

    }

    @Override
    public void onDisable() {

    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            npcManager.sendAddNpc((Player) sender,new BasePlayerNpc((Player)sender));
        }
        return true;
    }
}
