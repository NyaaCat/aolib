package cat.nyaa.aolib.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RunCommandUtils {

    private static LoadingCache<String, List<String>> permissionCache = CacheBuilder
            .newBuilder()
            .concurrencyLevel(1)
            .maximumSize(1000)
            .build(CacheLoader.from(RunCommandUtils::parsePermission));

    private static List<String> parsePermission(String str) {
        return Arrays.asList(str.split(";"));
    }

    //https://github.com/NyaaCat/RPGItems-reloaded/blob/1.16/src/main/java/think/rpgitems/power/impl/Command.java
    public static String handlePlayerPlaceHolder(Player player, String cmd) {
        cmd = cmd.replaceAll("\\{player}", player.getName());
        cmd = cmd.replaceAll("\\{player\\.x}", Double.toString(player.getLocation().getX()));
        cmd = cmd.replaceAll("\\{player\\.y}", Double.toString(player.getLocation().getY()));
        cmd = cmd.replaceAll("\\{player\\.z}", Double.toString(player.getLocation().getZ()));
        cmd = cmd.replaceAll("\\{player\\.yaw}", Float.toString(90 + player.getEyeLocation().getYaw()));
        cmd = cmd.replaceAll("\\{player\\.pitch}", Float.toString(-player.getEyeLocation().getPitch()));
        cmd = cmd.replaceAll("\\{yaw}", Float.toString(player.getLocation().getYaw() + 90));
        cmd = cmd.replaceAll("\\{pitch}", Float.toString(-player.getLocation().getPitch()));
        return cmd;
    }

    public static void executeCommand(Player player, String command, @Nullable String commandPermission, Plugin plugin) {
        if (command == null || command.length() == 0 || player == null) return;

        String permission = Objects.requireNonNullElse(commandPermission, "");
        String cmd = handlePlayerPlaceHolder(player, command);

        if (!player.isOnline()) return;
        if (permission.equals("console")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            return;
        }

        if (permission.equals("*")) {
            boolean wasOp = player.isOp();
            try {
                player.setOp(true);
                player.performCommand(cmd);
            } finally {
                if (!wasOp) {
                    player.setOp(false);
                }
            }
        } else {
            List<PermissionAttachment> permissionAttachmentList = attachPermission(player, permission, plugin);
            try {
                player.performCommand(cmd);
            } finally {
                if (permissionAttachmentList != null)
                    for (PermissionAttachment attachment : permissionAttachmentList) {
                        player.removeAttachment(attachment);
                    }
            }
        }
    }

    @Nullable
    public static List<PermissionAttachment> attachPermission(Player player, String permissions, Plugin plugin) {
        if (player == null || permissions == null || permissions.length() == 0 || permissions.equals("*")) {
            return null;
        }
        List<String> permissionList = permissionCache.getUnchecked(permissions);
        List<PermissionAttachment> permissionAttachmentList = new ArrayList<>();
        for (String permission : permissionList) {
            if (permission.length() == 0) continue;
            if (player.hasPermission(permission)) {
                continue;
            }
            PermissionAttachment attachment = player.addAttachment(plugin, 1);
            if (attachment == null) continue;
            String[] perms = permission.split("\\.");
            StringBuilder p = new StringBuilder();
            for (String perm : perms) {
                p.append(perm);
                attachment.setPermission(p.toString(), true);
                p.append('.');
            }
            permissionAttachmentList.add(attachment);
        }
        return permissionAttachmentList;
    }
}