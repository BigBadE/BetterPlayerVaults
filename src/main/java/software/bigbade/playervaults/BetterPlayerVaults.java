package software.bigbade.playervaults;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.command.VaultCommand;
import software.bigbade.playervaults.impl.VaultManager;
import software.bigbade.playervaults.listener.VaultCloseListener;
import software.bigbade.playervaults.loading.IVaultLoader;
import software.bigbade.playervaults.loading.LibraryLoader;
import software.bigbade.playervaults.managers.LoaderManager;
import software.bigbade.playervaults.managers.MessageManager;
import software.bigbade.playervaults.managers.MetricsManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BetterPlayerVaults extends JavaPlugin {
    @Getter
    private static Logger pluginLogger;
    private int version;
    private FileConfiguration configuration;
    @Getter
    private IVaultLoader vaultLoader;
    private IVaultManager vaultManager;

    //Required for MockBukkit
    public BetterPlayerVaults() {
        super();
    }

    //Required for MockBukkit
    protected BetterPlayerVaults(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        version = Integer.parseInt(Bukkit.getVersion().split("\\.")[1]);

        setPluginLogger(getLogger());

        configuration = getConfig();

        MessageManager messageManager = new MessageManager();
        messageManager.loadMessages(getDataFolder());
        messageManager.setMainLanguage(configuration.getString("language", "english"));
        loadVaultLoader();

        if (configuration.getBoolean("stats", true) && !Metrics.class.getPackage().getName().equals("org.bstats.bukkit")) {
            new MetricsManager(this);
        }

        if (vaultLoader == null) {
            return;
        }

        vaultManager = new VaultManager(vaultLoader);

        Bukkit.getPluginManager().registerEvents(new VaultCloseListener(vaultManager), this);
        Objects.requireNonNull(getCommand("playervault")).setExecutor(new VaultCommand(vaultManager));
    }

    private void loadVaultLoader() {
        String loader = configuration.getString("saveType", (version >= 14) ? "persistent" : "flatfile");
        Objects.requireNonNull(loader);
        LoaderManager loaderManager = new LoaderManager(this);
        if (!loaderManager.checkName(loader, version)) {
            getPluginLogger().log(Level.SEVERE, "Invalid saveType set! Options are: flatfile, persistent (1.14+), mysql, mongodb");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            if (loader.equals("mysql") || loader.equals("mongodb")) {
                Bukkit.getScheduler().runTaskAsynchronously(this, () -> new LibraryLoader(getDataFolder().getAbsolutePath()).loadLibrary(loader, getDownload(loader)));
            }
            vaultLoader = loaderManager.getVaultLoader(loader);
        }
    }

    private URL getDownload(String name) {
        try {
            if (name.equals("mysql")) {
                return new URL("https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.19/mysql-connector-java-8.0.19.jar");
            } else {
                return new URL("https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/4.0.2/mongodb-driver-sync-4.0.3.jar");
            }
        } catch (MalformedURLException e) {
            getPluginLogger().log(Level.SEVERE, "It seems the maven repo for the driver was deleted");
        }
        return null;
    }

    @Override
    public void onDisable() {
        if (vaultManager == null) {
            return;
        }
        for (IPlayerVault vault : vaultManager.getVaults()) {
            vaultManager.closeVault(vault);
        }
        vaultManager.clearVaults();
    }

    public static void setPluginLogger(Logger pluginLogger) {
        if(BetterPlayerVaults.pluginLogger == null) {
            BetterPlayerVaults.pluginLogger = pluginLogger;
        }
    }
}
