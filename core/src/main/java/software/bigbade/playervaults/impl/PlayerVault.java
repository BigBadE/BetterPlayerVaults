package software.bigbade.playervaults.impl;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;

@RequiredArgsConstructor
public class PlayerVault implements IPlayerVault {
  @Getter private final UUID owner;
  @Getter private final Inventory inventory;
  @Getter private final int number;

  private boolean opened = true;

  @Override
  public void toggleClosed() {
    opened = !opened;
  }
}
