package software.bigbade.playervaults.managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.api.IVaultLoader;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.impl.PlayerVault;
import software.bigbade.playervaults.messages.StringMessage;
import software.bigbade.playervaults.taskchain.ActionChain;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;

public class VaultManager implements IVaultManager {
    public static final StringMessage VAULT_TITLE =
            new StringMessage("title.vault");

    // List of last opened vaults for player
    private final Map<UUID, IPlayerVault> vaults = new WeakHashMap<>();

    private final IVaultLoader vaultLoader;

    public VaultManager(IVaultLoader vaultLoader) {
        this.vaultLoader = vaultLoader;
    }

    @Override
    public boolean isInVault(UUID player) {
        return getVault(player, -1).isPresent();
    }

    @Override
    public Optional<IPlayerVault> getVault(UUID player, int number) {
        return Optional.ofNullable(vaults.get(player))
                .filter(vault -> number == -1 || vault.getNumber() == number);
    }

    @Override
    public void openVault(UUID player, Player opener, int vaultNumber) {
        CompletableFuture<Inventory> future = vaultLoader.getVault(player, vaultNumber, 54);
        future.thenAccept(inventory -> {
            IPlayerVault vault =
                    new PlayerVault(opener.getUniqueId(), inventory, vaultNumber);
            vaults.put(player, vault);
            new ActionChain().sync(() -> opener.openInventory(inventory)).execute();
        });
    }

    @Override
    public void openVault(Player player, int vaultNumber) {
        Optional<IPlayerVault> optionalVault =
                getVault(player.getUniqueId(), vaultNumber);
        if (optionalVault.isPresent()) {
            IPlayerVault vault = optionalVault.get();
            vault.toggleClosed();
            player.openInventory(vault.getInventory());
        } else {
            vaultLoader.getVault(player, vaultNumber).thenAccept(inventory -> {
                IPlayerVault vault =
                        new PlayerVault(player.getUniqueId(), inventory, vaultNumber);
                vaults.put(player.getUniqueId(), vault);
                new ActionChain().sync(() -> player.openInventory(inventory)).execute();
            });
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
        if (worksOffline()) {
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
