package software.bigbade.playervaults.loading;

import lombok.SneakyThrows;
import software.bigbade.playervaults.BetterPlayerVaults;
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
    private final File libraryFolder;

    public LibraryLoader(String dataFolder) {
        libraryFolder = new File(dataFolder, "libraries");
        if(!libraryFolder.exists()) {
            FileUtils.createDirectory(libraryFolder);
        }
    }

    @SneakyThrows
    public void loadLibrary(String name, URL download) {
        File file = new File(libraryFolder, name + ".jar");
        if(!file.exists()) {
            LibraryLoader.downloadJar(file, download);
        }
        URL[] urls = new URL[] { new URL("jar:file:" + file.getAbsolutePath() + "!/") };
        try (URLClassLoader classLoader = new URLClassLoader(urls, LibraryLoader.class.getClassLoader())) {
            LibraryLoader.loadJar(file, classLoader);
        }
    }

    private static void loadJar(File file, URLClassLoader classLoader) {
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
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not load external jar file", e);
        }
    }

    private static void downloadJar(File file, URL download) {
        InputStream stream = FileUtils.createStream(download);
        if(stream == null) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not download library! manually download it from {0} and name it {1} and put it in the libraries folder.", new Object[] { download, file.getName() });
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
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not open input stream", e);
        }
    }

    private static void loadClass(JarEntry jar, ClassLoader loader) throws ClassNotFoundException {
        String className = jar.getName().substring(0, jar.getName().length() - 6);
        className = className.replace('/', '.');
        loader.loadClass(className);
    }
}
