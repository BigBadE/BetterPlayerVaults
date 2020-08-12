package software.bigbade.playervaults.command;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.messages.StringMessage;

@RequiredArgsConstructor
public abstract class BasicCommand<T extends CommandSender>
    implements CommandExecutor {
  public static final String PLAYER_NOT_FOUND_MESSAGE =
      new StringMessage("command.player-not-found").translate();
  public static final String OFFLINE_PLAYER_MESSAGE =
      new StringMessage("command.offline-player").translate();
  public static final String TOO_MANY_ARGS_MESSAGE =
      new StringMessage("command.too-many-args").translate();
  public static final String NOT_A_PLAYER_MESSAGE =
      new StringMessage("command.not-a-player").translate();
  public static final String NO_PERMISSION_MESSAGE =
      new StringMessage("command.permission").translate();
  public static final StringMessage NOT_A_NUMBER_MESSAGE =
      new StringMessage("command.not-a-number");

  @Nullable private final String permission;
  @Getter private final IVaultManager vaultManager;

  @SuppressWarnings("unchecked")
  @Override
  public boolean onCommand(@Nonnull CommandSender sender,
                           @Nonnull Command command, @Nonnull String label,
                           @Nonnull String[] args) {
    if (permission != null && !sender.hasPermission(permission) &&
        !sender.isOp()) {
      sender.sendMessage(NO_PERMISSION_MESSAGE);
      return true;
    }
    try {
      onCommand((T)sender, args);
    } catch (ClassCastException e) {
      sender.sendMessage(NOT_A_PLAYER_MESSAGE);
    }
    return true;
  }

  public abstract void onCommand(T sender, String[] args);

  public Optional<Integer> parseInt(String number, T sender) {
    try {
      return Optional.of(Integer.parseInt(number));
    } catch (NumberFormatException e) {
      sender.sendMessage(NOT_A_NUMBER_MESSAGE.translate(number));
      return Optional.empty();
    }
  }

  public Optional<Player> getPlayer(String name) {
    return Optional.ofNullable(Bukkit.getPlayer(name));
  }

  public void getPlayer(T player, String name, Consumer<Player> onlinePlayer,
                        Consumer<OfflinePlayer> offlinePlayer) {
    Optional<Player> targetOptional = getPlayer(name);
    if (targetOptional.isPresent()) {
      onlinePlayer.accept(targetOptional.get());
    } else if (!vaultManager.worksOffline()) {
      player.sendMessage(BasicCommand.OFFLINE_PLAYER_MESSAGE);
    } else {
      Optional<OfflinePlayer> playerOptional = getOfflinePlayer(name, player);
      playerOptional.ifPresent(offlinePlayer);
    }
  }

  @SuppressWarnings("deprecation")
  public Optional<OfflinePlayer> getOfflinePlayer(String name, T player) {
    OfflinePlayer target = Bukkit.getOfflinePlayer(name);
    if (!target.hasPlayedBefore()) {
      player.sendMessage(PLAYER_NOT_FOUND_MESSAGE);
      return Optional.empty();
    } else {
      return Optional.of(target);
    }
  }
}
