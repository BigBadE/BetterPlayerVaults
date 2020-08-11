package software.bigbade.playervaults.command;

import org.bukkit.entity.Player;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.messages.StringMessage;
import software.bigbade.playervaults.utils.VaultSizeUtil;

import javax.annotation.Nonnull;
import java.util.Optional;

public class VaultCommand extends BasicCommand<Player> {
    public static final StringMessage OTHER_NOT_ENOUGH_VAULTS_MESSAGE = new StringMessage("command.vault.other-not-enough");
    public static final StringMessage OPEN_OTHER_VAULT_MESSAGE = new StringMessage("command.vault.open-other-vault");
    public static final StringMessage NOT_ENOUGH_VAULTS_MESSAGE = new StringMessage("command.vault.not-enough");
    public static final String COMMAND_OP_USAGE_MESSAGE = new StringMessage("command.vault.op-usage").translate();
    public static final String COMMAND_USAGE_MESSAGE = new StringMessage("command.vault.usage").translate();
    public static final String OPEN_VAULT_MESSAGE = new StringMessage("command.vault.open").translate();
    private static final String SNOOP_PERMISSION = "betterplayervaults.snoop";

    public VaultCommand(IVaultManager vaultManager) {
        super("betterplayervaults.open", vaultManager);
    }

    @Override
    public void onCommand(Player player, @Nonnull String[] args) {
        Optional<Integer> vaultId = Optional.of(1);
        if (args.length > 2 || args.length == 2 && !player.hasPermission(SNOOP_PERMISSION) && !player.isOp()) {
            player.sendMessage(TOO_MANY_ARGS_MESSAGE);
            player.sendMessage(player.isOp() || player.hasPermission(SNOOP_PERMISSION) ? COMMAND_OP_USAGE_MESSAGE : COMMAND_USAGE_MESSAGE);
            return;
        } else if (args.length == 2) {
            vaultId = parseInt(args[1], player);
            vaultId.ifPresent(vault -> getPlayer(player, args[0], target -> {
                int vaults = VaultSizeUtil.getVaults(target);
                if (vaults < vault) {
                    player.sendMessage(OTHER_NOT_ENOUGH_VAULTS_MESSAGE.translate(target.getName(), vaults));
                    return;
                }
                getVaultManager().openVault(target.getUniqueId(), player, vault);
                player.sendMessage(OPEN_OTHER_VAULT_MESSAGE.translate(target.getName()));
            }, target -> {
                getVaultManager().openVault(target.getUniqueId(), player, vault);
                player.sendMessage(OPEN_OTHER_VAULT_MESSAGE.translate(target.getName()));
            }));
            return;
        } else if (args.length == 1) {
            vaultId = parseInt(args[0], player);
        }
        System.out.println("Checking vault");
        vaultId.ifPresent(vault -> {
            System.out.println("Yep");
            int vaults = VaultSizeUtil.getVaults(player);
            if (vaults < vault) {
                player.sendMessage(NOT_ENOUGH_VAULTS_MESSAGE.translate(vaults));
                return;
            }
            System.out.println("Opening");
            getVaultManager().openVault(player, vault);
            player.sendMessage(OPEN_VAULT_MESSAGE);
        });
    }
}
