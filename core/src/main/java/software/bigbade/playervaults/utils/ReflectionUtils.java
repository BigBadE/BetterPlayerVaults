package software.bigbade.playervaults.utils;

import software.bigbade.playervaults.PlayerVaults;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.logging.Level;

public final class ReflectionUtils {
    private ReflectionUtils() {
    }

    public static Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            PlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not get field {0}", name);
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        try {
            Method method = clazz.getDeclaredMethod(name, args);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            PlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not get method {0}", name);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Method method, Object target, Object... args) {
        try {
            return (T) method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            PlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not call method", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(Field field, Object target) {
        try {
            return (T) field.get(target);
        } catch (IllegalAccessException e) {
            PlayerVaults.getPluginLogger().log(Level.SEVERE, "Error getting field", e);
            return null;
        }
    }

    public static void setValue(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            PlayerVaults.getPluginLogger().log(Level.SEVERE, "Error getting field", e);
        }
    }

    public static <T> T instantiate(Class<T> clazz, Object... args) {
        Class<?>[] params = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            params[i] = args[i].getClass();
        }
        try {
            return Objects.requireNonNull(getConstructor(clazz, params)).newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            PlayerVaults.getPluginLogger().log(Level.SEVERE, "Error instantiating class", e);
        }
        return null;
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... params) {
        Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor(params);
        } catch (NoSuchMethodException e) {
            PlayerVaults.getPluginLogger().log(Level.SEVERE, "Error getting constructor", e);
            return null;
        }
        constructor.setAccessible(true);
        return constructor;
    }
}
