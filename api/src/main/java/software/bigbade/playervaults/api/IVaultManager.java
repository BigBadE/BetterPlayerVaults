package software.bigbade.playervaults.api;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.entity.Player;

public interface IVaultManager {
  boolean isInVault(UUID player);

  void openVault(Player player, int vaultNumber);

  void openVault(UUID player, Player opener, int vaultNumber);

  /**
   * Gets the vault of the player
   * @param player The player to get the vault of
   * @param number Vault number, -1 for any
   * @return Optional of vault
   */
  Optional<IPlayerVault> getVault(UUID player, int number);

  void closeVault(UUID player, IPlayerVault vault);

  void removeVault(UUID player);

  void clearVaults();

  void resetVault(Player player, int number);

  void resetVaultOffline(UUID player, int number);

  boolean worksOffline();
}
