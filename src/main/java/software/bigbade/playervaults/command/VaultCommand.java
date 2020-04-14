package software.bigbade.playervaults.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.loading.IVaultLoader;

import javax.annotation.Nonnull;
import java.util.Map;

@RequiredArgsConstructor
public class VaultCommand implements CommandExecutor {
    private final IVaultLoader loader;
    private final IVaultManager vaultManager;

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (!(commandSender instanceof Player))
            return false;
        if (!commandSender.hasPermission("betterplayervaults.open") && !commandSender.isOp()) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
            return true;
        }
        Player player = (Player) commandSender;
        vaultManager.openVault(player, 1);
        Inventory inventory = Bukkit.createInventory(null, 27, "Vault 1");
        for (Map.Entry<Integer, ItemStack> entry : loader.getVault(player, 1).entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }
        player.openInventory(inventory);
        return true;
    }
}
