package software.bigbade.playervaults.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import javax.annotation.Nullable;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import software.bigbade.playervaults.PlayerVaults;

public final class FileUtils {
  private FileUtils() {}

  public static void delete(File file) {
    if (file.exists() && !file.delete()) {
      PlayerVaults.getPluginLogger().log(
          Level.SEVERE, "Could not delete file {0}", file.getAbsolutePath());
    }
  }

  public static void write(File file, byte[] data) {
    try {
      try (FileOutputStream outputStream = new FileOutputStream(file)) {
        outputStream.write(data);
      }
    } catch (IOException e) {
      PlayerVaults.getPluginLogger().log(Level.SEVERE,
                                         "Could not write to file", e);
    }
  }

  public static byte[] read(File file) {
    try {
      try (FileInputStream inputStream = new FileInputStream(file)) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];

        int i;
        while ((i = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, i);
        }
        return outputStream.toByteArray();
      }
    } catch (IOException e) {
      PlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not read file",
                                         e);
    }
    return new byte[0];
  }

  public static void copyURLToFile(URL url, File file) {
    try (InputStream stream = url.openStream();
         FileOutputStream fileOutputStream =
             new FileOutputStream(file, false)) {
      byte[] buffer = new byte[1024];
      int read;
      while ((read = stream.read(buffer)) != -1) {
        fileOutputStream.write(buffer, 0, read);
      }
    } catch (IOException e) {
      PlayerVaults.getPluginLogger().log(
          Level.SEVERE, "Error downloading file from {0}", url);
    }
  }

  public static boolean compareURLContents(URL url, byte[] content) {
    try (InputStream stream = url.openStream()) {
      byte[] buffer = new byte[content.length];
      return stream.read(buffer) != content.length || stream.available() != 0 ||
          !Arrays.equals(buffer, content);
    } catch (IOException e) {
      PlayerVaults.getPluginLogger().log(
          Level.SEVERE, "Error downloading file from {0}", url);
    }
    return false;
  }

  public static YamlConfiguration loadYamlFile(File file) {
    YamlConfiguration configuration = new YamlConfiguration();
    try {
      configuration.load(file);
    } catch (IOException | InvalidConfigurationException e) {
      PlayerVaults.getPluginLogger().log(
          Level.SEVERE,
          "Error loading translation file: Not a translation file", e);
    }
    return configuration;
  }

  public static URL getURL(String path) {
    try {
      return new URL(path);
    } catch (MalformedURLException e) {
      PlayerVaults.getPluginLogger().log(Level.SEVERE, e,
                                         () -> "Could not get URL " + path);
    }
    return null;
  }

  public static void createDirectory(File file) {
    if (!file.mkdirs()) {
      PlayerVaults.getPluginLogger().log(Level.SEVERE,
                                         "Could not create data directory {0}",
                                         file.getAbsolutePath());
    }
  }

  @Nullable
  public static InputStream createStream(URL url) {
    try {
      return url.openStream();
    } catch (IOException e) {
      PlayerVaults.getPluginLogger().log(Level.SEVERE,
                                         "Could not connect to URL", e);
    }
    return null;
  }
}
