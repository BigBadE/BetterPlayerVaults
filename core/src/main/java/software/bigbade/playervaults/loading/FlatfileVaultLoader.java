package software.bigbade.playervaults.loading;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.api.IVaultLoader;
import software.bigbade.playervaults.serialization.SerializationUtils;
import software.bigbade.playervaults.taskchain.ActionChain;
import software.bigbade.playervaults.utils.FileUtils;
import software.bigbade.playervaults.utils.VaultSizeUtil;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FlatfileVaultLoader implements IVaultLoader {
    private final File dataFolder;

    public FlatfileVaultLoader(String folderPath) {
        dataFolder = new File(folderPath, "vaults");
        if (!dataFolder.exists()) {
            FileUtils.createDirectory(dataFolder);
        }
    }

    @Override
    public CompletableFuture<Inventory> getVault(Player player, int vault) {
        return getVault(player.getUniqueId(), vault, VaultSizeUtil.getSize(player));
    }

    @Override
    public CompletableFuture<Inventory> getVault(UUID player, int vault,
                                                 int size) {
        CompletableFuture<Inventory> future = new CompletableFuture<>();
        new ActionChain(player.toString())
                .async(() -> {
                    File dataFile = new File(dataFolder, player.toString() + vault);
                    if (!dataFile.exists()) {
                        future.complete(Bukkit.createInventory(
                                null, size, ExternalDataLoader.VAULT_TITLE.translate(vault)));
                    }
                    future.complete(SerializationUtils.deserialize(
                            FileUtils.read(dataFile),
                            ExternalDataLoader.VAULT_TITLE.translate(vault), size));
                })
                .execute();

        return future;
    }

    @Override
    public void saveVault(UUID player, IPlayerVault vault) {
        new ActionChain(player.toString())
                .async(() -> {
                    File file =
                            new File(dataFolder, player.toString() + vault.getNumber());
                    FileUtils.delete(file);
                    FileUtils.write(file,
                            SerializationUtils.serialize(vault.getInventory()));
                })
                .execute();
    }

    @Override
    public String getName() {
        return "flatfile";
    }

    @Override
    public void resetVault(Player player, int number) {
        resetVault(player.getUniqueId(), number);
    }

    @Override
    public void resetVault(UUID player, int number) {
        new ActionChain(player.toString()).async(() -> {
            File file = new File(dataFolder, player.toString() + number);
            FileUtils.delete(file);
        }).execute();
    }

    @Override
    public boolean worksOffline() {
        return true;
    }

    @Override
    public void shutdown() {
        // Not needed, VaultManager does this
    }
}
