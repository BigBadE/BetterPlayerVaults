package software.bigbade.playervaults.managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.impl.PlayerVault;
import software.bigbade.playervaults.loading.IVaultLoader;
import software.bigbade.playervaults.messages.StringMessage;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

public class VaultManager implements IVaultManager {
    public static final StringMessage VAULT_TITLE = new StringMessage("title.vault");

    //List of last opened vaults for player
    private final Map<UUID, IPlayerVault> vaults = new WeakHashMap<>();

    private final IVaultLoader vaultLoader;

    public VaultManager(IVaultLoader vaultLoader) {
        this.vaultLoader = vaultLoader;
    }

    @Override
    public boolean isInVault(UUID player) {
        return getVault(player).isPresent();
    }

    @Override
    public Optional<IPlayerVault> getVault(UUID player) {
        return Optional.ofNullable(vaults.get(player));
    }

    @Override
    public void openVault(UUID player, Player opener, int vaultNumber) {
        Inventory inventory = vaultLoader.getVault(player, vaultNumber, 54);
        IPlayerVault vault = new PlayerVault(opener.getUniqueId(), inventory, vaultNumber);
        vaults.put(player, vault);
        opener.openInventory(inventory);
    }

    @Override
    public void openVault(Player player, int vaultNumber) {
        Optional<IPlayerVault> optionalVault = getVault(player.getUniqueId());
        if (optionalVault.isPresent()) {
            optionalVault.get().toggleClosed();
        } else {
            Inventory inventory = vaultLoader.getVault(player, vaultNumber);
            IPlayerVault vault = new PlayerVault(player.getUniqueId(), inventory, vaultNumber);
            vaults.put(player.getUniqueId(), vault);
            player.openInventory(inventory);
        }
    }

    @Override
    public void closeVault(UUID player, IPlayerVault vault) {
        vaultLoader.saveVault(player, vault);
        vault.toggleClosed();
    }

    @Override
    public void removeVault(UUID player) {
        vaults.remove(player);
    }

    @Override
    public void clearVaults() {
        vaults.forEach(vaultLoader::saveVault);
        vaults.clear();
    }

    @Override
    public void resetVault(Player player, int number) {
        if(worksOffline()) {
            resetVaultOffline(player.getUniqueId(), number);
        } else {
            vaultLoader.resetVault(player, number);
        }
    }

    @Override
    public void resetVaultOffline(UUID player, int number) {
        vaultLoader.resetVault(player, number);
    }

    @Override
    public boolean worksOffline() {
        return vaultLoader.worksOffline();
    }
}
