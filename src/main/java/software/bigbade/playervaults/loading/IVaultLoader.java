package software.bigbade.playervaults.loading;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import software.bigbade.playervaults.api.IPlayerVault;

import java.util.Map;

public interface IVaultLoader {
    Inventory getVault(Player player, int vault);

    void saveVault(IPlayerVault vault);

    String getName();
}