package software.bigbade.playervaults.loading;

import lombok.SneakyThrows;
import software.bigbade.playervaults.BetterPlayerVaults;
import software.bigbade.playervaults.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class LibraryLoader {
    private final String libraryFolder;

    public LibraryLoader(String dataFolder) {
        libraryFolder = dataFolder + "\\libraries\\";
        if(!Files.isDirectory(Paths.get(libraryFolder))) {
            FileUtils.createDirectory(Paths.get(libraryFolder));
        }
    }

    @SneakyThrows
    public void loadLibrary(String name, URL download) {
        String path = libraryFolder + name + ".jar";
        if(!Files.exists(Paths.get(path + ".jar"))) {
            downloadJar(name, download);
        }
        URL[] urls = new URL[] { new URL("jar:file:" + path + "!/") };
        try (URLClassLoader classLoader = new URLClassLoader(urls, LibraryLoader.class.getClassLoader())) {
            LibraryLoader.loadJar(path, classLoader);
        }
    }

    private static void loadJar(String path, URLClassLoader classLoader) {
        try (JarFile jarFile = new JarFile(path)) {
            Enumeration<JarEntry> enumerator = jarFile.entries();
            while (enumerator.hasMoreElements()) {
                JarEntry jar = enumerator.nextElement();
                if (jar.isDirectory() || !jar.getName().endsWith(".class")) {
                    continue;
                }

                loadClass(jar, classLoader);
            }
        } catch (IOException | ClassNotFoundException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not load external jar file", e);
        }
    }

    private void downloadJar(String name, URL download) {
        InputStream stream = FileUtils.createStream(download);
        if(stream == null) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not download library! manually download it from {0} and name it {1} and put it in the libraries folder.", new Object[] { download, name});
            return;
        }
        try (BufferedInputStream inputStream = new BufferedInputStream(stream);
             FileOutputStream fileOutputStream = new FileOutputStream(libraryFolder + name + ".jar")) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not open input stream", e);
        }
    }

    private static void loadClass(JarEntry jar, ClassLoader loader) throws ClassNotFoundException {
        String className = jar.getName().substring(0, jar.getName().length() - 6);
        className = className.replace('/', '.');
        loader.loadClass(className);
    }
}
