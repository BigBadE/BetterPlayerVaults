package software.bigbade.playervaults.loading;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;

public interface IVaultLoader {
    Inventory getVault(Player player, int vault);

    Inventory getVault(OfflinePlayer player, int vault);

    void saveVault(IPlayerVault vault);

    String getName();

    boolean worksOffline();
}