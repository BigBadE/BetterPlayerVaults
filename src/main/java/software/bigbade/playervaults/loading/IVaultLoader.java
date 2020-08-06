package software.bigbade.playervaults.loading;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;

import java.util.UUID;

public interface IVaultLoader {
    Inventory getVault(Player player, int vault);

    Inventory getVault(UUID player, int vault, int size);

    void saveVault(UUID player, IPlayerVault vault);

    String getName();

    void resetVault(Player player, int number);

    void resetVault(UUID player, int number);

    boolean worksOffline();
}