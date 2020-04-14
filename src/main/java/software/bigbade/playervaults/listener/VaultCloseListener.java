package software.bigbade.playervaults.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.impl.PlayerVault;
import software.bigbade.playervaults.loading.IVaultLoader;

@RequiredArgsConstructor
public class VaultCloseListener implements Listener {
    private final IVaultLoader loader;
    private final IVaultManager vaultManager;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        IPlayerVault vault = vaultManager.getVault((Player) event.getPlayer());
        if(vault != null) {
            loader.saveVault(vault);
        }
    }
}
