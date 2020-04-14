package software.bigbade.playervaults.impl;

import jdk.internal.jline.internal.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.loading.IVaultLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class VaultManager implements IVaultManager {
    @Getter
    private final List<IPlayerVault> vaults = new ArrayList<>();

    private final IVaultLoader vaultLoader;

    public boolean isInVault(Player player) {
        return getVault(player) == null;
    }

    @Nullable
    public IPlayerVault getVault(Player player) {
        for(IPlayerVault vault : vaults) {
            if(vault.getPlayer().equals(player))
                return vault;
        }
        return null;
    }

    public void openVault(Player player, Map<Integer, ItemStack> items, int vaultNumber) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Vault " + vaultNumber);
        items.forEach(inventory::setItem);
        IPlayerVault vault = new PlayerVault(player, inventory, vaultNumber);
        vaults.add(vault);
        player.openInventory(inventory);
    }

    public void closeVault(IPlayerVault vault) {
        vault.getPlayer().closeInventory();
        vaultLoader.saveVault(vault);
        vaults.remove(vault);
    }

    public void clearVaults() {
        vaults.clear();
    }
}
