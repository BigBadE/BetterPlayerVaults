package software.bigbade.playervaults.loading;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.utils.FileUtils;
import software.bigbade.playervaults.utils.SerializationUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FlatfileVaultLoader implements IVaultLoader {
    private final String dataFolder;

    public FlatfileVaultLoader(String dataFolder) {
        this.dataFolder = dataFolder + "\\vaults\\";
    }

    @Override
    public Map<Integer, ItemStack> getVault(Player player, int vault) {
        Path path = Paths.get(dataFolder + player.getUniqueId().toString());
        if (!Files.exists(path))
            return new HashMap<>();
        return SerializationUtils.deserialize(new String(FileUtils.read(path)));
    }

    @Override
    public void saveVault(IPlayerVault vault) {
        Path path = Paths.get(dataFolder + vault.getPlayer().getUniqueId().toString());
        FileUtils.delete(path);
        Inventory inventory = vault.getInventory();
        Map<Integer, ItemStack> items = new HashMap<>();
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack found = inventory.getItem(i);
            if(found != null) {
                items.put(i, found);
            }
        }
        FileUtils.write(path, SerializationUtils.serialize(items));
    }
}
