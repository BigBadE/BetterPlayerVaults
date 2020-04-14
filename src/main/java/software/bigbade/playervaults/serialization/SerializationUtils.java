package software.bigbade.playervaults.serialization;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import software.bigbade.playervaults.BetterPlayerVaults;
import software.bigbade.playervaults.utils.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
 *
 * @see SerializationTypes
 */
public final class SerializationUtils {
    private static final Method deserializeMeta;
    private static final Class<?> deserializeMetaClass;

    static {
        //Load deserialization method from CraftMetaItem
        deserializeMetaClass = ReflectionUtils.getCraftBukkitClass("inventory.CraftMetaItem$SerializableMeta");
        Objects.requireNonNull(deserializeMetaClass);
        deserializeMeta = ReflectionUtils.getMethod(deserializeMetaClass, "deserialize");
    }

    private SerializationUtils() {
    }

    /**
     * Deserializes given data
     *
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
     *
     * @param current Serialized map
     * @return Deserialized version
     */
    public static Map<String, Object> deserializeMap(String current) {
        Map<String, Object> itemData = new HashMap<>();
        while (!current.startsWith("}") && !current.startsWith("]")) {
            String[] split = current.split("=", 2);
            itemData.put(split[0], deserializeValue(split[1]));
            current = current.split(";", 2)[1];
        }
        return itemData;
    }

    private static Object deserializeValue(String serializedValue) {
        for (SerializationLists lists : SerializationLists.values()) {
            if (serializedValue.startsWith(lists.getSymbol())) {
                return lists.deserialize(serializedValue);
            }
        }
        return null;
    }

    /**
     * Deserializes an item meta using reflection
     *
     * @param data Serialized item meta
     * @return deserialized item meta
     */
    public static ItemMeta getMeta(Map<String, Object> data) {
        try {
            return (ItemMeta) deserializeMeta.invoke(null, data);
        } catch (IllegalAccessException | InvocationTargetException e) {
            BetterPlayerVaults.getPluginLogger().log(Level.SEVERE, "Could not serialize data", e);
        }
        return null;
    }

    /**
     * Serializes vault
     *
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
     *
     * @param builder Where the key/value pair should be witten to
     * @param key     The key
     * @param value   The value
     */
    public static void serialize(StringBuilder builder, String key, Object value) {
        builder.append(key).append("=");
        for (SerializationLists lists : SerializationLists.values()) {
            if (lists.getClazz().isAssignableFrom(value.getClass())) {
                lists.serialize(builder, value);
                builder.append(";");
                return;
            }
        }
    }
}
