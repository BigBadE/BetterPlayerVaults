package software.bigbade.playervaults;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.api.IVaultManager;
import software.bigbade.playervaults.command.VaultCommand;
import software.bigbade.playervaults.listener.VaultCloseListener;
import software.bigbade.playervaults.loading.IVaultLoader;
import software.bigbade.playervaults.utils.LoaderManager;
import software.bigbade.playervaults.utils.MetricsManager;
import software.bigbade.playervaults.impl.VaultManager;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BetterPlayerVaults extends JavaPlugin {
    private int version;

    private FileConfiguration configuration;

    private IVaultLoader vaultLoader;

    private IVaultManager vaultManager;

    @Getter
    private static Logger pluginLogger;

    @Override
    public void onEnable() {
        version = Integer.parseInt(Bukkit.getVersion().split("\\.")[1]);

        setPluginLogger(getLogger());

        configuration = getConfig();
        if(configuration.getBoolean("stats", true)) {
            new MetricsManager(this);
        }

        loadVaultLoader();
        if(vaultLoader == null)
            return;

        vaultManager = new VaultManager(vaultLoader);

        Bukkit.getPluginManager().registerEvents(new VaultCloseListener(vaultLoader, vaultManager), this);
        Objects.requireNonNull(getCommand("playervault")).setExecutor(new VaultCommand(vaultLoader, vaultManager));
    }

    private static void setPluginLogger(Logger pluginLogger) {
        if(BetterPlayerVaults.pluginLogger != null)
            throw new IllegalStateException("Tried to set already-set logger!");
        BetterPlayerVaults.pluginLogger = pluginLogger;
    }

    private void loadVaultLoader() {
        String loader = configuration.getString("saveType", (version >= 14) ? "persistent" : "flatfile");
        Objects.requireNonNull(loader);
        LoaderManager loaderManager = new LoaderManager(this);
        if(!loaderManager.checkName(loader, version)) {
            getPluginLogger().log(Level.SEVERE, "Invalid saveType set! Options are: flatfile, persistent (1.14+)");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            vaultLoader = loaderManager.getVaultLoader(loader);
        }
    }

    @Override
    public void onDisable() {
        for(IPlayerVault vault : vaultManager.getVaults()) {
            vaultManager.closeVault(vault);
        }
        vaultManager.clearVaults();
    }
}
