package software.bigbade.playervaults.loading;

import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import software.bigbade.playervaults.PlayerVaults;
import software.bigbade.playervaults.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class LibraryLoader {
    private final ConfigurationSection section;
    private final File libraryFolder;

    public LibraryLoader(String dataFolder, ConfigurationSection section) {
        this.section = section;
        libraryFolder = new File(dataFolder, "libraries");
        if (!libraryFolder.exists()) {
            FileUtils.createDirectory(libraryFolder);
        }
    }

    private static String getDriverClass(String name) {
        if (name.equals("mysql")) {
            return "java.sql.Connection";
        } else if (name.equals("mongo")) {
            return "com.mongodb.client.MongoClient";
        }
        throw new IllegalArgumentException("Unknown driver " + name);
    }

    private void loadJar(File file, URLClassLoader classLoader) {
        try (JarFile jarFile = new JarFile(file)) {
            Enumeration<JarEntry> enumerator = jarFile.entries();
            while (enumerator.hasMoreElements()) {
                JarEntry jar = enumerator.nextElement();
                if (jar.isDirectory() || !jar.getName().endsWith(".class")) {
                    continue;
                }

                loadClass(jar, classLoader);
            }
        } catch (IOException | ClassNotFoundException e) {
            PlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not load external jar file", e);
        }
    }

    private static void downloadJar(File file, URL download) {
        InputStream stream = FileUtils.createStream(download);
        if (stream == null) {
            PlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not download library! manually download it from {0} and name it {1} and put it in the libraries folder.", new Object[]{download, file.getName()});
            return;
        }
        try (BufferedInputStream inputStream = new BufferedInputStream(stream);
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            PlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not open input stream", e);
        }
    }

    private void loadClass(JarEntry jar, ClassLoader loader) throws ClassNotFoundException {
        String className = jar.getName().substring(0, jar.getName().length() - 6);
        className = className.replace('/', '.');
        loader.loadClass(className);
    }

    @SneakyThrows
    public void loadLibrary(String name, URL[] downloads) {
        if (section.getBoolean("use-shared-libraries")) {
            try {
                Class.forName(LibraryLoader.getDriverClass(name), false, getClass().getClassLoader());
                PlayerVaults.getPluginLogger().log(Level.INFO, "The library for {0} detected in classpath. If any ClassNotFoundExceptions are thrown, disable shared libs in the config.", name);
                return;
            } catch (ClassNotFoundException ignored) {
                PlayerVaults.getPluginLogger().log(Level.INFO, "No {0} library found, manually getting it", name);
            }
        }
        URL[] urls = new URL[downloads.length];
        File[] files = new File[downloads.length];
        int number = 1;
        for (URL download : downloads) {
            File file = new File(libraryFolder, name + "-" + number + ".jar");
            if (!file.exists()) {
                LibraryLoader.downloadJar(file, download);
            }
            files[number - 1] = file;
            urls[number - 1] = new URL("jar:file:" + file.getAbsolutePath() + "!/");
            number++;
        }

        try (URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader())) {
            for (File file : files) {
                loadJar(file, classLoader);
            }
        }
    }
}
