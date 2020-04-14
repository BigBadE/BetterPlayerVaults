package software.bigbade.playervaults.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
<<<<<<< HEAD
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.api.IVaultManager;
=======
import software.bigbade.playervaults.impl.PlayerVault;
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
import software.bigbade.playervaults.loading.IVaultLoader;

@RequiredArgsConstructor
public class VaultCloseListener implements Listener {
    private final IVaultLoader loader;
<<<<<<< HEAD
    private final IVaultManager vaultManager;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        IPlayerVault vault = vaultManager.getVault((Player) event.getPlayer());
        if(vault != null) {
            loader.saveVault(vault);
=======

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(event.getView().getTitle().startsWith("Vault ")) {
            loader.saveVault(new PlayerVault((Player) event.getPlayer(), event.getInventory(), Integer.parseInt(event.getView().getTitle().substring(6))));
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
        }
    }
}
