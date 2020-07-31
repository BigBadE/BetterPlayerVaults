package software.bigbade.playervaults.loading;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;

public class MySQLVaultLoader implements IVaultLoader {
    @Override
    public Inventory getVault(Player player, int vault) {
        return null;
    }

    @Override
    public void saveVault(IPlayerVault vault) {
        //TODO
    }

    @Override
    public String getName() {
        return "mysql";
    }
}
