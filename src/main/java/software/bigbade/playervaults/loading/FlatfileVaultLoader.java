package software.bigbade.playervaults.loading;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.serialization.SerializationUtils;
import software.bigbade.playervaults.utils.FileUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FlatfileVaultLoader implements IVaultLoader {
    private final String dataFolder;

    public FlatfileVaultLoader(String dataFolder) {
        this.dataFolder = dataFolder + "\\vaults\\";
        if (!Files.isDirectory(Paths.get(dataFolder))) {
            FileUtils.createDirectory(Paths.get(dataFolder));
        }
    }

    @Override
    public Inventory getVault(Player player, int vault) {
        Path path = Paths.get(dataFolder + player.getUniqueId().toString());
        if (!Files.exists(path)) {
            return Bukkit.createInventory(null, 27, "Vault " + vault);
        }
        return SerializationUtils.deserialize(new String(FileUtils.read(path), StandardCharsets.UTF_8));
    }

    @Override
    public void saveVault(IPlayerVault vault) {
        Path path = Paths.get(dataFolder + vault.getPlayer().getUniqueId().toString());
        FileUtils.delete(path);
        FileUtils.write(path, SerializationUtils.serialize(vault.getInventory(), "Vault " + vault.getNumber()));
    }

    @Override
    public String getName() {
        return "flatfile";
    }
}
