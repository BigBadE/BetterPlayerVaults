package software.bigbade.playervaults.messages;

import org.bukkit.ChatColor;
import software.bigbade.playervaults.managers.MessageManager;

import java.util.regex.Pattern;

public class StringMessage {
    private static final Pattern ARG_PATTERN = Pattern.compile("%s");

    private final String message;

    public StringMessage(String key) {
        message = ChatColor.translateAlternateColorCodes('&', MessageManager.getInstance().getMessage(key));
    }

    public String translate(String... args) {
        String translation = message;
        for(String arg : args) {
            translation = ARG_PATTERN.matcher(translation).replaceFirst(arg);
        }
        return translation;
    }
}
