package software.bigbade.playervaults.loading;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.serialization.SerializationUtils;
import software.bigbade.playervaults.utils.FileUtils;

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
        File dataFile = new File(dataFolder, player.getUniqueId().toString());
        if (!dataFile.exists()) {
            return Bukkit.createInventory(null, 27, "Vault " + vault);
        }
        return SerializationUtils.deserialize(FileUtils.read(dataFile));
    }

    @Override
    public void saveVault(IPlayerVault vault) {
        File file = new File(dataFolder, vault.getOwner().toString());
        FileUtils.delete(file);
        FileUtils.write(file, SerializationUtils.serialize(vault.getInventory(), "Vault " + vault.getNumber()));
    }

    @Override
    public String getName() {
        return "flatfile";
    }
}
