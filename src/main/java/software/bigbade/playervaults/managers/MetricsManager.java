package software.bigbade.playervaults.managers;

import org.bstats.bukkit.Metrics;
import software.bigbade.playervaults.BetterPlayerVaults;

public class MetricsManager {
    public MetricsManager(BetterPlayerVaults main) {
        Metrics metrics = new Metrics(main, 7137);
        metrics.addCustomChart(new Metrics.SimplePie("saveMethod", main.getVaultLoader()::getName));
    }
}
