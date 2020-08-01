package software.bigbade.playervaults.messages;

import org.bukkit.ChatColor;
import software.bigbade.playervaults.BetterPlayerVaults;
import software.bigbade.playervaults.managers.MessageManager;

import java.util.logging.Level;
import java.util.regex.Pattern;

public class StringMessage {
    private static final Pattern ARG_PATTERN = Pattern.compile("%s");

    private final String message;

    public StringMessage(String key) {
        String foundMessage = MessageManager.getInstance().getMessage(key);
        if(foundMessage == null) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not find translation for key {0}", key);
            message = key;
        } else {
            message = ChatColor.translateAlternateColorCodes('&', foundMessage);
        }
    }

    public String translate(String... args) {
        String translation = message;
        for(String arg : args) {
            translation = ARG_PATTERN.matcher(translation).replaceFirst(arg);
        }
        return translation;
    }
}
