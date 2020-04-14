package software.bigbade.playervaults.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
<<<<<<< HEAD
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.loading.IVaultLoader;

import javax.annotation.Nonnull;
=======
import software.bigbade.playervaults.loading.IVaultLoader;

>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
import java.util.Map;

@RequiredArgsConstructor
public class VaultCommand implements CommandExecutor {
    private final IVaultLoader loader;
<<<<<<< HEAD
    private final IVaultManager vaultManager;

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
=======

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
        if(!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        Inventory inventory = Bukkit.createInventory(null, 27, "Vault 1");
        for(Map.Entry<Integer, ItemStack> entry : loader.getVault(player, 1).entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }
        player.openInventory(inventory);
        return true;
    }
}
