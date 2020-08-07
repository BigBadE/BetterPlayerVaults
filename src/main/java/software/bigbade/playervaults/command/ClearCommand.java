package software.bigbade.playervaults.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.messages.StringMessage;

import java.util.Optional;

public class ClearCommand extends BasicCommand<CommandSender> {
    private static final String VAULT_CLEARED_MESSAGE = new StringMessage("command.clear.success").translate();
    public static final String COMMAND_USAGE_MESSAGE = new StringMessage("command.clear.usage").translate();

    public ClearCommand(IVaultManager vaultManager) {
        super("betterplayervaults.clear", vaultManager);
    }

    @Override
    public void onCommand(CommandSender player, String[] args) {
        if(args.length == 1) {
            if(player instanceof Player) {
                Optional<Integer> vaultOptional = parseInt(args[0], player);
                vaultOptional.ifPresent(vault -> {
                    getVaultManager().resetVault((Player) player, vault);
                    player.sendMessage(VAULT_CLEARED_MESSAGE);
                });
            } else {
                player.sendMessage(VaultCommand.NOT_A_PLAYER_MESSAGE);
            }
        } else if(args.length == 2) {
            Optional<Integer> vaultOptional = parseInt(args[1], player);
            vaultOptional.ifPresent(vault -> getPlayer(player, args[0], target -> {
                getVaultManager().resetVault(target, vault);
                player.sendMessage(VAULT_CLEARED_MESSAGE);
            }, target -> {
                getVaultManager().resetVaultOffline(target.getUniqueId(), vault);
                player.sendMessage(VAULT_CLEARED_MESSAGE);
            }));
        } else {
            player.sendMessage(VaultCommand.TOO_MANY_ARGS_MESSAGE);
            player.sendMessage(COMMAND_USAGE_MESSAGE);
        }
    }
}
