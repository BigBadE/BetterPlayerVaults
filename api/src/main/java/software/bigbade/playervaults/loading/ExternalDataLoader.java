package software.bigbade.playervaults.loading;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.PlayerVaults;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.api.IVaultLoader;
import software.bigbade.playervaults.messages.StringMessage;
import software.bigbade.playervaults.serialization.SerializationUtils;
import software.bigbade.playervaults.taskchain.ActionChain;
import software.bigbade.playervaults.utils.VaultSizeUtil;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public abstract class ExternalDataLoader implements IVaultLoader {
    public static final StringMessage VAULT_TITLE = new StringMessage("title.vault");
    @Getter
    private static final Logger logger = PlayerVaults.getPluginLogger();

    @Override
    public CompletableFuture<Inventory> getVault(Player player, int vault) {
        return getVault(player.getUniqueId(), vault, VaultSizeUtil.getSize(player));
    }

    @Override
    public CompletableFuture<Inventory> getVault(UUID player, int vault, int size) {
        CompletableFuture<Inventory> future = new CompletableFuture<>();
        new ActionChain(player.toString()).async(() -> {
            byte[] data = loadData(player, vault);
            if (data.length == 0) {
                future.complete(Bukkit.createInventory(null, size, VAULT_TITLE.translate(vault)));
            } else {
                future.complete(SerializationUtils.deserialize(data, VAULT_TITLE.translate(vault), size));
            }
        }).execute();
        return future;
    }

    public abstract byte[] loadData(UUID player, int vault);

    @Override
    public void saveVault(UUID player, IPlayerVault vault) {
        saveData(player, vault.getNumber(), SerializationUtils.serialize(vault.getInventory()));
    }

    public abstract void saveData(UUID player, int number, byte[] data);

    @Override
    public void resetVault(Player player, int number) {
        resetVault(player.getUniqueId(), number);
    }

    @Override
    public boolean worksOffline() {
        return true;
    }

    @Override
    public void shutdown() {
        //Overridable by subclasses
    }
}
