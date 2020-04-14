package software.bigbade.playervaults.loading;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import software.bigbade.playervaults.api.IPlayerVault;

import java.util.Map;

public class MySQLVaultLoader implements IVaultLoader {
    @Override
    public Map<Integer, ItemStack> getVault(Player player, int vault) {
        return null;
    }

    @Override
    public void saveVault(IPlayerVault vault) {

    }
}
