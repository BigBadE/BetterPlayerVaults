package software.bigbade.playervaults;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import software.bigbade.playervaults.command.VaultCommand;
import software.bigbade.playervaults.listener.VaultCloseListener;
import software.bigbade.playervaults.loading.IVaultLoader;
import software.bigbade.playervaults.utils.LoaderManager;
import software.bigbade.playervaults.utils.MetricsManager;

import java.util.Objects;
import java.util.logging.Level;

public class BetterPlayerVaults extends JavaPlugin {
    private int version;

    private FileConfiguration configuration;

    private IVaultLoader vaultLoader;

    @Override
    public void onEnable() {
        version = Integer.parseInt(Bukkit.getVersion().split("\\.")[1]);

        configuration = getConfig();
        if(configuration.getBoolean("stats", true)) {
            new MetricsManager(this);
        }

        loadVaultLoader();
        if(vaultLoader == null)
            return;

        Bukkit.getPluginManager().registerEvents(new VaultCloseListener(vaultLoader), this);
        getCommand("playervault").setExecutor(new VaultCommand(vaultLoader));
    }

    private void loadVaultLoader() {
        String loader = configuration.getString("saveType", (version >= 14) ? "persistent" : "flatfile");
        Objects.requireNonNull(loader);
        LoaderManager loaderManager = new LoaderManager(this);
        if(!loaderManager.checkName(loader, version)) {
            getLogger().log(Level.SEVERE, "Invalid saveType set! Options are: flatfile, persistent (1.14+)");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            vaultLoader = loaderManager.getVaultLoader(loader);
        }
    }

    @Override
    public void onDisable() {

    }
}
