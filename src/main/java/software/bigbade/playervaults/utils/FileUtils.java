package software.bigbade.playervaults.utils;

import software.bigbade.playervaults.BetterPlayerVaults;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public final class FileUtils {
    private FileUtils() {}

    public static void delete(Path path) {
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not delete file", e);
            }
        }
    }

    public static void write(Path path, String data) {
        try {
            Files.write(path, data.getBytes());
        } catch (IOException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not write to file", e);
        }
    }

    public static byte[] read(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not read file", e);
        }
        return new byte[0];
    }

    public static void createDirectory(Path path) {
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not create data directory", e);
        }
    }

    public static InputStream createStream(URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not connect to URL", e);
        }
        return null;
    }
}
