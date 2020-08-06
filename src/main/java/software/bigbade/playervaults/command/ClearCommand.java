package software.bigbade.playervaults.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.messages.StringMessage;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class ClearCommand implements CommandExecutor {
    private final IVaultManager vaultManager;

    private static final String VAULT_CLEARED_MESSAGE = new StringMessage("command.clear.success").translate();
    public static final String COMMAND_USAGE_MESSAGE = new StringMessage("command.clear.usage").translate();

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if(args.length == 1) {
            if(commandSender instanceof Player) {
                int vault = VaultCommand.getVaultFromString(args[0], commandSender);
                if(vault == -1) {
                    commandSender.sendMessage(VaultCommand.NOT_A_NUMBER_MESSAGE.translate(args[0]));
                    return true;
                }
                vaultManager.resetVault((Player) commandSender, vault);
                commandSender.sendMessage(VAULT_CLEARED_MESSAGE);
            } else {
                commandSender.sendMessage(VaultCommand.NOT_A_PLAYER_MESSAGE);
            }
        } else if(args.length == 2) {
            int vault = VaultCommand.getVaultFromString(args[1], commandSender);
            if(vault == -1) {
                commandSender.sendMessage(VaultCommand.NOT_A_NUMBER_MESSAGE.translate(args[1]));
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                if(!offlinePlayer.hasPlayedBefore() || !vaultManager.worksOffline()) {
                    commandSender.sendMessage(VaultCommand.PLAYER_NOT_FOUND_MESSAGE);
                    return true;
                }
                vaultManager.resetVaultOffline(offlinePlayer.getUniqueId(), vault);
            } else {
                vaultManager.resetVault(target, vault);
            }
            commandSender.sendMessage(VAULT_CLEARED_MESSAGE);
        } else {
            commandSender.sendMessage(VaultCommand.TOO_MANY_ARGS_MESSAGE);
            commandSender.sendMessage(COMMAND_USAGE_MESSAGE);
        }
        return true;
    }
}
