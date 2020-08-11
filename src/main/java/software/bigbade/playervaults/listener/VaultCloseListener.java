package software.bigbade.playervaults.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import software.bigbade.playervaults.api.IVaultManager;

@RequiredArgsConstructor
public class VaultCloseListener implements Listener {
    private final IVaultManager vaultManager;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(event.getInventory().getType() == InventoryType.CHEST) {
            vaultManager.getVault(event.getPlayer().getUniqueId(), -1).ifPresent(vault -> vaultManager.closeVault(event.getPlayer().getUniqueId(), vault));
        }
    }

    @EventHandler
    public void onPlayerKicked(PlayerKickEvent event) {
        removePlayerVault(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        removePlayerVault(event.getPlayer());
    }

    private void removePlayerVault(Player player) {
        vaultManager.removeVault(player.getUniqueId());
    }
}
