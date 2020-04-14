package software.bigbade.playervaults;

<<<<<<< HEAD
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.api.IVaultManager;
=======
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
import software.bigbade.playervaults.command.VaultCommand;
import software.bigbade.playervaults.listener.VaultCloseListener;
import software.bigbade.playervaults.loading.IVaultLoader;
import software.bigbade.playervaults.utils.LoaderManager;
import software.bigbade.playervaults.utils.MetricsManager;
<<<<<<< HEAD
import software.bigbade.playervaults.impl.VaultManager;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
=======

import java.util.Objects;
import java.util.logging.Level;
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874

public class BetterPlayerVaults extends JavaPlugin {
    private int version;

    private FileConfiguration configuration;

    private IVaultLoader vaultLoader;

<<<<<<< HEAD
    private IVaultManager vaultManager;

    @Getter
    private static Logger pluginLogger;

=======
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
    @Override
    public void onEnable() {
        version = Integer.parseInt(Bukkit.getVersion().split("\\.")[1]);

<<<<<<< HEAD
        setPluginLogger(getLogger());

=======
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
        configuration = getConfig();
        if(configuration.getBoolean("stats", true)) {
            new MetricsManager(this);
        }

        loadVaultLoader();
        if(vaultLoader == null)
            return;

<<<<<<< HEAD
        vaultManager = new VaultManager(vaultLoader);

        Bukkit.getPluginManager().registerEvents(new VaultCloseListener(vaultLoader, vaultManager), this);
        Objects.requireNonNull(getCommand("playervault")).setExecutor(new VaultCommand(vaultLoader, vaultManager));
    }

    private static void setPluginLogger(Logger pluginLogger) {
        if(BetterPlayerVaults.pluginLogger != null)
            throw new IllegalStateException("Tried to set already-set logger!");
        BetterPlayerVaults.pluginLogger = pluginLogger;
=======
        Bukkit.getPluginManager().registerEvents(new VaultCloseListener(vaultLoader), this);
        getCommand("playervault").setExecutor(new VaultCommand(vaultLoader));
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
    }

    private void loadVaultLoader() {
        String loader = configuration.getString("saveType", (version >= 14) ? "persistent" : "flatfile");
        Objects.requireNonNull(loader);
        LoaderManager loaderManager = new LoaderManager(this);
        if(!loaderManager.checkName(loader, version)) {
<<<<<<< HEAD
            getPluginLogger().log(Level.SEVERE, "Invalid saveType set! Options are: flatfile, persistent (1.14+)");
=======
            getLogger().log(Level.SEVERE, "Invalid saveType set! Options are: flatfile, persistent (1.14+)");
>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            vaultLoader = loaderManager.getVaultLoader(loader);
        }
    }

    @Override
    public void onDisable() {
<<<<<<< HEAD
        for(IPlayerVault vault : vaultManager.getVaults()) {
            vaultManager.closeVault(vault);
        }
        vaultManager.clearVaults();
=======

>>>>>>> 717768a3a7e522fd24fa46c46194f40c00773874
    }
}
