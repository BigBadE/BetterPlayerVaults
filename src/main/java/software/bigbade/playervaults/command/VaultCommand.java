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
import software.bigbade.playervaults.utils.VaultSizeUtil;

import javax.annotation.Nonnull;

@RequiredArgsConstructor
public class VaultCommand implements CommandExecutor {
    public static final StringMessage NOT_A_NUMBER_MESSAGE = new StringMessage("command.not-a-number");
    public static final StringMessage OPEN_OTHER_VAULT_MESSAGE = new StringMessage("command.vault.open-other-vault");
    public static final StringMessage NOT_ENOUGH_VAULTS_MESSAGE = new StringMessage("command.vault.not-enough");
    public static final StringMessage OTHER_NOT_ENOUGH_VAULTS_MESSAGE = new StringMessage("command.vault.other-not-enough");
    public static final String PLAYER_NOT_FOUND_MESSAGE = new StringMessage("command.player-not-found").translate();
    public static final String OFFLINE_PLAYER_MESSAGE = new StringMessage("command.offline-player").translate();
    public static final String NOT_A_PLAYER_MESSAGE = new StringMessage("command.player-not-found").translate();
    public static final String TOO_MANY_ARGS_MESSAGE = new StringMessage("command.too-many-args").translate();
    public static final String COMMAND_USAGE_MESSAGE = new StringMessage("command.vault.usage").translate();
    public static final String NO_PERMISSION_MESSAGE = new StringMessage("command.permission").translate();
    public static final String OPEN_VAULT_MESSAGE = new StringMessage("command.vault.open").translate();
    private final IVaultManager vaultManager;

    //TODO reduce cognitive complexity
    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(NOT_A_PLAYER_MESSAGE);
            return true;
        }
        int vaultId = 1;
        Player player = (Player) commandSender;
        if(args.length == 2) {
            if(!player.hasPermission("betterplayervaults.snoop") && !player.isOp()) {
                player.sendMessage(TOO_MANY_ARGS_MESSAGE);
                player.sendMessage(COMMAND_USAGE_MESSAGE);
            }
            Player target = Bukkit.getPlayer(args[0]);
            vaultId = VaultCommand.getVaultFromString(args[1], player);
            if(target == null) {
                if(vaultManager.worksOffline()) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    if(!offlinePlayer.hasPlayedBefore()) {
                        player.sendMessage(PLAYER_NOT_FOUND_MESSAGE);
                    } else {
                        vaultManager.openVault(offlinePlayer.getUniqueId(), player, vaultId);
                        player.sendMessage(OPEN_OTHER_VAULT_MESSAGE.translate(offlinePlayer.getName()));
                    }
                } else {
                    player.sendMessage(OFFLINE_PLAYER_MESSAGE);
                }
                return true;
            }
            if(vaultId == -1) {
                return true;
            }
            int vaults = VaultSizeUtil.getVaults(target);
            if(vaults < vaultId) {
                player.sendMessage(OTHER_NOT_ENOUGH_VAULTS_MESSAGE.translate(target.getName(), vaults));
                return true;
            }
            vaultManager.openVault(target.getUniqueId(), player, vaultId);
            player.sendMessage(OPEN_OTHER_VAULT_MESSAGE.translate(target.getName()));
            return true;
        } else if(args.length == 1) {
            vaultId = VaultCommand.getVaultFromString(args[0], player);
            if(vaultId == -1) {
                return true;
            }
        } else if(args.length >= 2) {
            player.sendMessage(TOO_MANY_ARGS_MESSAGE);
            player.sendMessage(COMMAND_USAGE_MESSAGE);
            return true;
        }
        if (!player.hasPermission("betterplayervaults.open") && !player.isOp()) {
            player.sendMessage(NO_PERMISSION_MESSAGE);
            return true;
        }
        int vaults = VaultSizeUtil.getVaults(player);
        if(vaults < vaultId) {
            player.sendMessage(NOT_ENOUGH_VAULTS_MESSAGE.translate(vaults));
            return true;
        }
        vaultManager.openVault(player, vaultId);
        player.sendMessage(OPEN_VAULT_MESSAGE);
        return true;
    }

    public static int getVaultFromString(String number, CommandSender player) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            player.sendMessage(NOT_A_NUMBER_MESSAGE.translate(number));
        }
        return -1;
    }
}
