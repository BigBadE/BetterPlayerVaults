package software.bigbade.playervaults.messages;

import java.util.logging.Level;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import software.bigbade.playervaults.PlayerVaults;
import software.bigbade.playervaults.api.IMessageManager;

public class StringMessage {
  private static final Pattern ARG_PATTERN = Pattern.compile("%s");

  private final String message;

  public StringMessage(String key) {
    String foundMessage = IMessageManager.getInstance().getMessage(key);
    if (foundMessage == null) {
      PlayerVaults.getPluginLogger().log(
          Level.SEVERE, "Could not find translation for key {0}", key);
      message = key;
    } else {
      message = ChatColor.translateAlternateColorCodes('&', foundMessage);
    }
  }

  public String translate(Object... args) {
    String translation = message;
    for (Object arg : args) {
      translation =
          ARG_PATTERN.matcher(translation).replaceFirst(arg.toString());
    }
    return translation;
  }
}
