package software.bigbade.playervaults;

import co.aikar.taskchain.TaskChain;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import software.bigbade.playervaults.api.IVaultLoader;
import software.bigbade.playervaults.utils.FileUtils;

public abstract class PlayerVaults extends JavaPlugin {
  private static final URL VERSION_URL = FileUtils.getURL(
      "https://raw.githubusercontent.com/BigBadE/BetterPlayerVaults/release/version.yml");
  private static final String VERSION_YML = "version.yml";
  @Getter private static Logger pluginLogger;

  @Setter(AccessLevel.PRIVATE) private static String version;
  @Getter @Setter(AccessLevel.PRIVATE) private static String latestVersion;

  // Required for MockBukkit
  public PlayerVaults() { super(); }

  // Required for MockBukkit
  protected PlayerVaults(JavaPluginLoader loader,
                         PluginDescriptionFile description, File dataFolder,
                         File file) {
    super(loader, description, dataFolder, file);
  }

  public static void setPluginLogger(Logger pluginLogger) {
    if (PlayerVaults.pluginLogger == null) {
      PlayerVaults.pluginLogger = pluginLogger;
    }
  }

  public void initVersion() {
    File versionFile = new File(getDataFolder(), VERSION_YML);
    if (!versionFile.exists()) {
      FileUtils.copyURLToFile(VERSION_URL, versionFile);
      setVersion("INSTALL");
    } else {
      byte[] data = FileUtils.read(versionFile);
      setVersion(
          new String(data, 10, data.length - 11, StandardCharsets.UTF_8));
    }
    assert VERSION_URL != null;
    try (InputStream stream = FileUtils.createStream(VERSION_URL)) {
      byte[] buffer = new byte[256];
      assert stream != null;
      int read = stream.read(buffer);
      String foundVersion =
          new String(buffer, 10, read - 11, StandardCharsets.UTF_8);
      if (foundVersion.equals("404: Not Found")) {
        foundVersion = version;
      }
      setLatestVersion(foundVersion);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static boolean shouldUpdate() {
    return !version.equals(latestVersion);
  }

  public abstract <T> TaskChain<T> newChain();

  public abstract <T> TaskChain<T> newSharedChain(String name);

  public abstract IVaultLoader getVaultLoader();
}
