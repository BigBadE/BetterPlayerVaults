package software.bigbade.playervaults.api;

import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface IVaultManager {
    boolean isInVault(UUID player);

    void openVault(Player player, int vaultNumber);

    void openVault(UUID player, Player opener, int vaultNumber);

    Optional<IPlayerVault> getVault(UUID player);

    void closeVault(UUID player, IPlayerVault vault);

    void removeVault(UUID player);

    void clearVaults();

    void resetVault(Player player, int number);

    void resetVaultOffline(UUID player, int number);

    boolean worksOffline();
}
