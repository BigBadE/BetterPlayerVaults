package software.bigbade.playervaults.utils;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import software.bigbade.playervaults.BetterPlayerVaults;

import java.util.Optional;
import java.util.logging.Level;

public final class VaultSizeUtil {
    private VaultSizeUtil() {}

    public static int getSize(Player player) {
        return VaultSizeUtil.getPermissionInteger(player, "betterplayervaults.vaults.size.", 27);
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
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Player {0} has invalid permission {1}", new Object[] { player.getName(), permission.get() });
            return defaultValue;
        }
    }
}
