package software.bigbade.playervaults.loading;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.managers.VaultManager;
import software.bigbade.playervaults.serialization.SerializationUtils;
import software.bigbade.playervaults.utils.FileUtils;
import software.bigbade.playervaults.utils.VaultSizeUtil;

import java.io.File;
import java.util.UUID;

public class FlatfileVaultLoader implements IVaultLoader {
    private final File dataFolder;

    public FlatfileVaultLoader(String folderPath) {
        dataFolder = new File(folderPath, "vaults");
        if (!dataFolder.exists()) {
            FileUtils.createDirectory(dataFolder);
        }
    }

    @Override
    public Inventory getVault(Player player, int vault) {
        return getVault(player.getUniqueId(), vault, VaultSizeUtil.getSize(player));
    }

    @Override
    public Inventory getVault(UUID player, int vault, int size) {
        File dataFile = new File(dataFolder, player.toString() + vault);
        if (!dataFile.exists()) {
            return Bukkit.createInventory(null, size, VaultManager.VAULT_TITLE.translate(size));
        }
        return SerializationUtils.deserialize(FileUtils.read(dataFile), VaultManager.VAULT_TITLE.translate(vault), size);
    }

    @Override
    public void saveVault(UUID player, IPlayerVault vault) {
        File file = new File(dataFolder, player.toString() + vault.getNumber());
        FileUtils.delete(file);
        FileUtils.write(file, SerializationUtils.serialize(vault.getInventory()));
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
        File file = new File(dataFolder, player.toString() + number);
        FileUtils.delete(file);
    }

    @Override
    public boolean worksOffline() {
        return true;
    }
}
