package software.bigbade.playervaults.utils;

import org.bukkit.Bukkit;
import software.bigbade.playervaults.BetterPlayerVaults;

import java.lang.reflect.Method;
import java.util.logging.Level;

public final class ReflectionUtils {
    private ReflectionUtils() {
    }

    public static String getCraftBukkitPackage() {
        return "org.bukkit.craftbukkit.v1_" + Integer.parseInt(Bukkit.getVersion().split("\\.")[1]) + "_R1";
    }

    public static Class<?> getCraftBukkitClass(String path) {
        try {
            return Class.forName(getCraftBukkitPackage() + "." + path);
        } catch (ClassNotFoundException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not load needed serialization class!", e);
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String name) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }
}
