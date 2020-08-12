package software.bigbade.playervaults;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import software.bigbade.playervaults.command.ClearCommand;
import software.bigbade.playervaults.command.VaultCommand;
import software.bigbade.playervaults.listener.VaultCloseListener;
import software.bigbade.playervaults.api.IVaultLoader;
import software.bigbade.playervaults.loading.LibraryLoader;
import software.bigbade.playervaults.managers.LoaderFactory;
import software.bigbade.playervaults.managers.MessageManager;
import software.bigbade.playervaults.managers.MetricsManager;
import software.bigbade.playervaults.managers.VaultManager;
import software.bigbade.playervaults.taskchain.ActionChain;
import software.bigbade.playervaults.utils.CompressionUtil;
import software.bigbade.playervaults.api.IVaultManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;

public class BetterPlayerVaults extends PlayerVaults {
    private final int version = Integer.parseInt(Bukkit.getVersion().split("\\.")[1]);

    private TaskChainFactory taskChainFactory;
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

        setPluginLogger(getLogger());

        configuration = getConfig();

        taskChainFactory = BukkitTaskChainFactory.create(this);

        loadVaultLoader();

        if (!isEnabled()) {
            return;
        }

        if (configuration.getBoolean("stats", true) && !Metrics.class.getPackage().getName().equals("org.bstats.bukkit")) {
            new MetricsManager(this);
        }
    }

    @Override
    public <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    @Override
    public <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }


    private void loadVaultLoader() {
        String loader = Objects.requireNonNull(configuration.getString("save-type", (version >= 14) ? "persistent" : "flatfile")).toLowerCase();
        Objects.requireNonNull(loader);
        LoaderFactory loaderFactory = new LoaderFactory(this);
        CompressionUtil.setCompressionLevel(getConfig().getInt("compression-level", 3));
        if (!loaderFactory.checkName(loader, version)) {
            getPluginLogger().log(Level.SEVERE, "Invalid saveType set! Options are: flatfile, persistent (1.14+), mysql, mongodb");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            if (loader.equals("mysql") || loader.equals("mongo")) {
                new ActionChain().async(() -> {
                    new LibraryLoader(getDataFolder().getAbsolutePath(), getConfig()).loadLibrary(loader, getDownload(loader));
                    vaultLoader = loaderFactory.getVaultLoader(loader);
                    finishLoading();
                }).execute();
            } else {
                vaultLoader = loaderFactory.getVaultLoader(loader);
                finishLoading();
            }
        }
    }

    private void finishLoading() {
        if (vaultLoader == null) {
            getPluginLogger().log(Level.SEVERE, "Error during initialization: Could not create vault loader.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        MessageManager messageManager = new MessageManager();

        messageManager.loadMessages(getDataFolder());
        messageManager.setMainLanguage(configuration.getString("language", "english"));

        vaultManager = new VaultManager(vaultLoader);

        Bukkit.getPluginManager().registerEvents(new VaultCloseListener(vaultManager), this);
        Objects.requireNonNull(getCommand("playervault")).setExecutor(new VaultCommand(vaultManager));
        Objects.requireNonNull(getCommand("clearvault")).setExecutor(new ClearCommand(vaultManager));
    }

    private URL[] getDownload(String name) {
        try {
            if (name.equals("mysql")) {
                return new URL[]{/*new URL(""),
                        new URL("https://repo1.maven.org/maven2/org/jboss/jbossas/jboss-as-connector/6.1.0.Final/jboss-as-connector-6.1.0.Final.jar"),
                        new URL("https://repo1.maven.org/maven2/com/mchange/mchange-commons-java/0.2.20/mchange-commons-java-0.2.20.jar"),
                        new URL("https://repo1.maven.org/maven2/com/mchange/c3p0/0.9.5.5/c3p0-0.9.5.5.jar"),*/
                        new URL("https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.21/mysql-connector-java-8.0.21.jar")};
            } else {
                //Libs must be loaded in order of dependency
                //Gson -> Core -> Sync
                return new URL[]{new URL("https://repo1.maven.org/maven2/org/mongodb/bson/4.1.0/bson-4.1.0.jar"),
                        new URL("https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-core/4.1.0/mongodb-driver-core-4.1.0.jar"),
                        new URL("https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/4.1.0/mongodb-driver-sync-4.1.0.jar")};
            }
        } catch (MalformedURLException e) {
            getPluginLogger().log(Level.SEVERE, "It seems the maven repo for the driver was deleted");
        }
        return new URL[0];
    }

    @Override
    public void onDisable() {
        if (vaultManager == null) {
            return;
        }
        vaultManager.clearVaults();
    }
}