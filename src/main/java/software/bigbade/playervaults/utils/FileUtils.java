package software.bigbade.playervaults.utils;

import software.bigbade.playervaults.BetterPlayerVaults;

import java.io.IOException;
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
}
