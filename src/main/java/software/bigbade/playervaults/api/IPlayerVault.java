package software.bigbade.playervaults.api;

import org.bukkit.inventory.Inventory;

import java.util.UUID;

public interface IPlayerVault {
    Inventory getInventory();

    int getNumber();

    UUID getOwner();
}
