package software.bigbade.playervaults.utils;

import org.bstats.bukkit.Metrics;
import software.bigbade.playervaults.BetterPlayerVaults;

public class MetricsManager {
    public MetricsManager(BetterPlayerVaults main) {
        Metrics metrics = new Metrics(main, 7137);
    }
}
