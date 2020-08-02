package software.bigbade.playervaults.loading;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.impl.VaultManager;
import software.bigbade.playervaults.serialization.SerializationUtils;
import software.bigbade.playervaults.utils.FileUtils;
import software.bigbade.playervaults.utils.VaultSizeUtil;

import java.io.File;

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
        return getVault(player, vault, VaultSizeUtil.getSize(player));
    }

    @Override
    public Inventory getVault(OfflinePlayer player, int vault, int size) {
        File dataFile = new File(dataFolder, player.getUniqueId().toString() + vault);
        if (!dataFile.exists()) {
            return Bukkit.createInventory(null, size, VaultManager.VAULT_TITLE.translate(size));
        }
        return SerializationUtils.deserialize(FileUtils.read(dataFile), size);
    }

    @Override
    public void saveVault(IPlayerVault vault) {
        File file = new File(dataFolder, vault.getOwner().toString() + vault.getNumber());
        FileUtils.delete(file);
        FileUtils.write(file, SerializationUtils.serialize(vault.getInventory(), VaultManager.VAULT_TITLE.translate(vault.getNumber())));
    }

    @Override
    public String getName() {
        return "flatfile";
    }

    @Override
    public boolean worksOffline() {
        return true;
    }
}
