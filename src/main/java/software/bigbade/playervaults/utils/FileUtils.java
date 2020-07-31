package software.bigbade.playervaults.utils;

import software.bigbade.playervaults.BetterPlayerVaults;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
