package software.bigbade.playervaults.api;

import org.bukkit.entity.Player;

import java.util.List;

public interface IVaultManager {
    boolean isInVault(Player player);

    void openVault(Player player, int vaultNumber);

    IPlayerVault getVault(Player player);

    List<IPlayerVault> getVaults();

    void closeVault(IPlayerVault vault);

    void clearVaults();
}
