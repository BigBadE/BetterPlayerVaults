package software.bigbade.playervaults.managers;

import org.bstats.bukkit.Metrics;
import software.bigbade.playervaults.PlayerVaults;
import software.bigbade.playervaults.api.IMessageManager;

public class MetricsManager {
  public MetricsManager(PlayerVaults main) {
    Metrics metrics = new Metrics(main, 7137);
    metrics.addCustomChart(
        new Metrics.SimplePie("saveMethod", main.getVaultLoader()::getName));
    metrics.addCustomChart(new Metrics.SimplePie(
        "language", IMessageManager.getInstance()::getLanguage));
  }
}
