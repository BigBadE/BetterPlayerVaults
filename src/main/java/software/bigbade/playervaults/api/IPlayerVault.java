package software.bigbade.playervaults.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface IPlayerVault {
    Inventory getInventory();

    int getNumber();

    Player getPlayer();
}
