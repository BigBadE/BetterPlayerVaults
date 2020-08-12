package software.bigbade.playervaults;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import software.bigbade.playervaults.command.ClearCommand;
import software.bigbade.playervaults.command.VaultCommand;
import software.bigbade.playervaults.impl.TaskChainFactoryImpl;
import software.bigbade.playervaults.listener.VaultCloseListener;
import software.bigbade.playervaults.api.IVaultLoader;
import software.bigbade.playervaults.loading.LibraryLoader;
import software.bigbade.playervaults.managers.LoaderFactory;
import software.bigbade.playervaults.managers.MessageManager;
import software.bigbade.playervaults.managers.MetricsManager;
import software.bigbade.playervaults.managers.VaultManager;
import software.bigbade.playervaults.taskchain.ActionChain;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.utils.CompressionUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;

public class BetterPlayerVaults extends PlayerVaults {
    private static final int MC_VERSION = Integer.parseInt(Bukkit.getVersion().split("\\.")[1]);

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

        new TaskChainFactoryImpl(this);

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
        String loader = Objects.requireNonNull(configuration.getString("save-type", (MC_VERSION >= 14) ? "persistent" : "flatfile")).toLowerCase();
        Objects.requireNonNull(loader);
        LoaderFactory loaderFactory = new LoaderFactory(this);
        CompressionUtil.setCompressionLevel(getConfig().getInt("compression-level", 3));
        if (!loaderFactory.checkName(loader, MC_VERSION)) {
            getPluginLogger().log(Level.SEVERE, "Invalid saveType set! Options are: flatfile, persistent (1.14+), mysql, mongodb");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            if (loader.equals("mysql") || loader.equals("mongo")) {
                new ActionChain().async(() -> {
                    LibraryLoader libraryLoader = new LibraryLoader(getDataFolder().getAbsolutePath(), getConfig());
                    libraryLoader.loadLibrary(loader, BetterPlayerVaults.getDownload(loader));
                    ConfigurationSection section = configuration.getConfigurationSection("database");
                    if(section == null) {
                        section = configuration.createSection("database");
                    }
                    vaultLoader = libraryLoader.getVaultLoader(section);
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

    private static URL getDownload(String name) {
        try {
            if (name.equals("mysql")) {
                return new URL("https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.21/mysql-connector-java-8.0.21.jar");
            } else {
                return new URL("https://repo1.maven.org/maven2/org/mongodb/bson/4.1.0/bson-4.1.0.jar");
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
        vaultManager.clearVaults();
    }
}