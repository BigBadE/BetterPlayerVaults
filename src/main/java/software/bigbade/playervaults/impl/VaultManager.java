package software.bigbade.playervaults.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.loading.IVaultLoader;
import software.bigbade.playervaults.messages.StringMessage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class VaultManager implements IVaultManager {
    public static final StringMessage VAULT_TITLE = new StringMessage("title.vault");

    @Getter
    private final List<IPlayerVault> vaults = new ArrayList<>();

    private final IVaultLoader vaultLoader;

    public boolean isInVault(Player player) {
        return getVault(player) == null;
    }

    @Nullable
    public IPlayerVault getVault(Player player) {
        for(IPlayerVault vault : vaults) {
            if(vault.getOwner().equals(player.getUniqueId())) {
                return vault;
            }
        }
        return null;
    }

    @Override
    public void openVault(OfflinePlayer player, Player opener, int vaultNumber) {
        Inventory inventory = vaultLoader.getVault(player, vaultNumber, 54);
        IPlayerVault vault = new PlayerVault(opener.getUniqueId(), inventory, vaultNumber);
        vaults.add(vault);
        opener.openInventory(inventory);
    }

    public void openVault(Player player, int vaultNumber) {
        Inventory inventory = vaultLoader.getVault(player, vaultNumber);
        IPlayerVault vault = new PlayerVault(player.getUniqueId(), inventory, vaultNumber);
        vaults.add(vault);
        player.openInventory(inventory);
    }

    public void closeVault(IPlayerVault vault) {
        vaultLoader.saveVault(vault);
        vaults.remove(vault);
    }

    public void clearVaults() {
        vaults.forEach(vaultLoader::saveVault);
        vaults.clear();
    }

    @Override
    public boolean worksOffline() {
        return vaultLoader.worksOffline();
    }
}
