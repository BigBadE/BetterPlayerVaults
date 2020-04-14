package software.bigbade.playervaults.loading;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import software.bigbade.playervaults.api.IPlayerVault;

import java.util.Map;

public interface IVaultLoader {
    Map<Integer, ItemStack> getVault(Player player, int vault);

    void saveVault(IPlayerVault vault);
}
