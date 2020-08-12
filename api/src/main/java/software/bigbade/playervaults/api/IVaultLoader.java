package software.bigbade.playervaults.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IVaultLoader {
    CompletableFuture<Inventory> getVault(Player player, int vault);

    CompletableFuture<Inventory> getVault(UUID player, int vault, int size);

    void saveVault(UUID player, IPlayerVault vault);

    String getName();

    void resetVault(Player player, int number);

    void resetVault(UUID player, int number);

    boolean worksOffline();

    void shutdown();
}