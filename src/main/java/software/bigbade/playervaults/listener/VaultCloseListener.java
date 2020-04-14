package software.bigbade.playervaults.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import software.bigbade.playervaults.impl.PlayerVault;
import software.bigbade.playervaults.loading.IVaultLoader;

@RequiredArgsConstructor
public class VaultCloseListener implements Listener {
    private final IVaultLoader loader;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(event.getView().getTitle().startsWith("Vault ")) {
            loader.saveVault(new PlayerVault((Player) event.getPlayer(), event.getInventory(), Integer.parseInt(event.getView().getTitle().substring(6))));
        }
    }
}
