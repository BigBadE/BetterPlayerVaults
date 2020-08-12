package software.bigbade.playervaults;

import co.aikar.taskchain.TaskChain;
import lombok.Getter;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import software.bigbade.playervaults.api.IVaultLoader;

import java.io.File;
import java.util.logging.Logger;

public abstract class PlayerVaults extends JavaPlugin {
    @Getter
    private static Logger pluginLogger;

    public static final String VERSION = "1.0";

    //Required for MockBukkit
    public PlayerVaults() {
        super();
    }

    //Required for MockBukkit
    protected PlayerVaults(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public static void setPluginLogger(Logger pluginLogger) {
        if (PlayerVaults.pluginLogger == null) {
            PlayerVaults.pluginLogger = pluginLogger;
        }
    }

    public abstract <T> TaskChain<T> newChain();

    public abstract <T> TaskChain<T> newSharedChain(String name);

    public abstract IVaultLoader getVaultLoader();
}
