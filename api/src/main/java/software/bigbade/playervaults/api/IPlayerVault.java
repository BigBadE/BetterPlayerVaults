package software.bigbade.playervaults.api;

import org.bukkit.inventory.Inventory;

public interface IPlayerVault {
  Inventory getInventory();

  int getNumber();

  void toggleClosed();
}
