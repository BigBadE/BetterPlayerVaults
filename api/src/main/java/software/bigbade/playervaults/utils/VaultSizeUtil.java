package software.bigbade.playervaults.utils;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import software.bigbade.playervaults.PlayerVaults;

import java.util.Optional;
import java.util.logging.Level;

public final class VaultSizeUtil {
    private VaultSizeUtil() {}

    public static int getSize(Player player) {
        int size = VaultSizeUtil.getPermissionInteger(player, "betterplayervaults.vaults.size.", 27);
        size = Math.min(Math.max(9, size), 54);
        if (size % 9 != 0) {
            //Round size to the nearest multiple of 9 using integer division
            size = 9 * size / 9;
        }
        return size;
    }

    public static int getVaults(Player player) {
        return VaultSizeUtil.getPermissionInteger(player, "betterplayervaults.vaults.amount.", 1);
    }

    private static int getPermissionInteger(Player player, String permissionStr, int defaultValue) {
        Optional<String> permission = player.getEffectivePermissions().stream()
                .filter(PermissionAttachmentInfo::getValue)
                .map(PermissionAttachmentInfo::getPermission)
                .filter(string -> string.startsWith(permissionStr))
                .findAny();
        try {
            return permission.map(string -> string.substring(permissionStr.length())).map(Integer::parseInt).orElse(defaultValue);
        } catch (NumberFormatException e) {
            PlayerVaults.getPluginLogger().log(Level.SEVERE, "Player {0} has invalid permission {1}", new Object[] { player.getName(), permission.get() });
            return defaultValue;
        }
    }
}
