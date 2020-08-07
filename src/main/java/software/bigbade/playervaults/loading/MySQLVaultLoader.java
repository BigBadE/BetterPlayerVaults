package software.bigbade.playervaults.loading;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.utils.VaultSizeUtil;

import java.util.UUID;

public class MySQLVaultLoader implements IVaultLoader {
    @Override
    public Inventory getVault(Player player, int vault) {
        return getVault(player.getUniqueId(), vault, VaultSizeUtil.getSize(player));
    }

    @Override
    public Inventory getVault(UUID player, int vault, int size) {
        //TODO
        return null;
    }

    @Override
    public void saveVault(UUID player, IPlayerVault vault) {
        //TODO
    }

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public void resetVault(Player player, int number) {
        //TODO
    }

    @Override
    public void resetVault(UUID player, int number) {
        //TODO
    }

    @Override
    public boolean worksOffline() {
        return true;
    }
}
