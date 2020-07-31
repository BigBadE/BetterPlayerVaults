package software.bigbade.playervaults.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.api.IVaultManager;

@RequiredArgsConstructor
public class VaultCloseListener implements Listener {
    private final IVaultManager vaultManager;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        IPlayerVault vault = vaultManager.getVault((Player) event.getPlayer());
        if (vault != null) {
            vaultManager.closeVault(vault);
        }
    }
}
