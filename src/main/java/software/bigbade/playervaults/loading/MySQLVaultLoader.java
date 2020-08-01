package software.bigbade.playervaults.loading;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;

public class MySQLVaultLoader implements IVaultLoader {
    @Override
    public Inventory getVault(Player player, int vault) {
        return getVault(player, vault);
    }

    @Override
    public Inventory getVault(OfflinePlayer player, int vault) {
        //TODO
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

    @Override
    public boolean worksOffline() {
        return true;
    }
}
