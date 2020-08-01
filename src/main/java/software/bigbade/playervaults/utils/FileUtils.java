package software.bigbade.playervaults.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import software.bigbade.playervaults.BetterPlayerVaults;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public final class FileUtils {
    private FileUtils() {
    }

    public static void delete(File file) {
        if (file.exists() && !file.delete()) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not delete file {0}", file.getAbsolutePath());
        }
    }

    public static void write(File file, byte[] data) {
        try {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(data);
            }
        } catch (IOException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not write to file", e);
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
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not read file", e);
        }
        return new byte[0];
    }

    public static void copyURLToFile(URL url, File file) {
        try(InputStream stream = url.openStream();
            FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = stream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Error downloading file from {0}", url);
        }
    }

    public static YamlConfiguration loadYamlFile(File file) {
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Error loading translation file: Not a translation file", e);
        }
        return configuration;
    }

    public static URL getURL(String path) {
        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, e, () -> "Could not get URL " + path);
        }
        return null;
    }

    public static void createDirectory(File file) {
        if (!file.mkdirs()) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not create data directory {0}", file.getAbsolutePath());
        }
    }

    @Nullable
    public static InputStream createStream(URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not connect to URL", e);
        }
        return null;
    }
}
