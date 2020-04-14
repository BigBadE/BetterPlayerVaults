package software.bigbade.playervaults.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface IVaultManager {
    boolean isInVault(Player player);

    void openVault(Player player, Map<Integer, ItemStack> items, int vaultNumber);

    IPlayerVault getVault(Player player);

    List<IPlayerVault> getVaults();

    void closeVault(IPlayerVault vault);

    void clearVaults();
}
