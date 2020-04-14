package software.bigbade.playervaults.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import software.bigbade.playervaults.BetterPlayerVaults;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

/**
 * Handles serialization for the entire plugin.
 * Format:
 * [#; Start of item, where # is the slot
 * = Between the key and value
 * ; Marks end of value
 * | Marks beginning/end of non-list value
 * {} Marks beginning/end of list value
 * { Marks beginning of ItemMeta
 * ] Marks end of item
 */
public final class SerializationUtils {
    private static Method deserializeMeta;

    static {
        try {
            //Load deserialization method from CraftMetaItem
            for (Method method : Class.forName("org.bukkit.craftbukkit.v1_" + Integer.parseInt(Bukkit.getVersion().split("\\.")[1]) + "_R1.inventory.CraftMetaItem$SerializableMeta").getMethods()) {
                if (method.getName().equals("deserialize")) {
                    deserializeMeta = method;
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not load needed serialization class!", e);
        }
    }

    private SerializationUtils() { }

    /**
     * Deserializes given data
     * @param data Serialized data
     * @return Map of slot numbers and itemstacks
     */
    public static Map<Integer, ItemStack> deserialize(@Nonnull String data) {
        Map<Integer, ItemStack> found = new HashMap<>();
        String current = data;
        while (current.startsWith("[")) {
            current = current.replaceFirst("\\[", "");
            int slot = Integer.parseInt(current.split(";", 2)[0]);
            current = current.split(";", 2)[1];
            Map<String, Object> map = deserializeMap(current);
            found.put(slot, ItemStack.deserialize(map));
            current = current.split("]", 2)[1];
        }
        return found;
    }

    /**
     * Takes a serialized map and deserializes it
     * @param current Serialized map
     * @return Deserialized version
     */
    private static Map<String, Object> deserializeMap(String current) {
        Map<String, Object> itemData = new HashMap<>();
        while (!current.startsWith("}") && !current.startsWith("]")) {
            String[] split = current.split("=", 2);
            if (split[1].startsWith("{@")) {
                itemData.put(split[0], getMeta(deserializeMap(split[1].substring(2))));
            } else if (split[1].startsWith("{")) {
                itemData.put(split[0], deserializeMap(split[1].substring(1)));
            } else {
                String value = split[1].substring(1).split("\\|;", 2)[0];
                for (SerializationTypes types : SerializationTypes.values()) {
                    if (value.startsWith(types.getSymbol())) {
                        itemData.put(split[0], types.getCast().apply(value.substring(1)));
                        value = null;
                        break;
                    }
                }
                if (value != null) {
                    value = value.replace("\uD83E\uDED9", "|");
                    itemData.put(split[0], value);
                }
            }
            current = current.split(";", 2)[1];
        }
        return itemData;
    }

    /**
     * Deserializes an item meta using reflection
     * @param data Serialized item meta
     * @return deserialized item meta
     */
    private static ItemMeta getMeta(Map<String, Object> data) {
        try {
            return (ItemMeta) deserializeMeta.invoke(null, data);
        } catch (IllegalAccessException | InvocationTargetException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not serialize data", e);
        }
        return null;
    }

    /**
     * Serializes vault
     * @param data map of slots and the items in the slot
     * @return Serialized vault
     */
    public static String serialize(Map<Integer, ItemStack> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, ItemStack> entry : data.entrySet()) {
            builder.append("[").append(entry.getKey()).append(";");
            for (Map.Entry<String, Object> itemEntry : entry.getValue().serialize().entrySet()) {
                serialize(builder, itemEntry.getKey(), itemEntry.getValue());
            }
            builder.append("]");
        }
        return builder.toString();
    }

    /**
     * serialized key/value pair and adds it to given builder
     * @param builder Where the key/value pair should be witten to
     * @param key The key
     * @param value The value
     */
    @SuppressWarnings("unchecked")
    private static void serialize(StringBuilder builder, String key, Object value) {
        builder.append(key).append("=");
        if (value instanceof Map) {
            builder.append("{");
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                serialize(builder, entry.getKey(), entry.getValue());
            }
            builder.append("}");
        } else if (value instanceof ItemMeta) {
            builder.append("{@");
            for (Map.Entry<String, Object> entry : ((ItemMeta) value).serialize().entrySet()) {
                serialize(builder, entry.getKey(), entry.getValue());
            }
            builder.append("}");
        } else if (value instanceof Collection) {
            builder.append("|&");
            Iterator<?> iterator = ((Collection<?>) value).iterator();
            while (true) {
                builder.append(iterator.next().toString().replace("|", "\uD83E\uDED9"));
                if(iterator.hasNext())
                    builder.append("|");
                else
                    break;
            }
            builder.append("|");
        } else {
            builder.append("|");
            for (SerializationTypes types : SerializationTypes.values()) {
                if (types.getClazz().isAssignableFrom(value.getClass())) {
                    builder.append(types.getSymbol());
                }
            }
            builder.append(value.toString().replace("|", "\uD83E\uDED9")).append("|");
        }
        builder.append(";");
    }
}
