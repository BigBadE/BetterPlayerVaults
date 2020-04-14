package software.bigbade.playervaults.utils;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles serialization for the entire plugin.
 * Format:
 * [#; Start of item, where # is the slot
 * = Between the key and value
 * ; Marks end of value
 * | Marks beginning/end of non-list value
 * {} Marks beginning/end of list value
 * ] Marks end of item
 */
public final class SerializationManager {
    private SerializationManager() {
    }

    public static Map<Integer, ItemStack> deserialize(@Nonnull String data) {
        System.out.println("Deserializing " + data);
        Map<Integer, ItemStack> found = new HashMap<>();
        String current = data;
        while (current.startsWith("[")) {
            current = current.replaceFirst("\\[", "");
            int slot = Integer.parseInt(current.split(";", 2)[0]);
            current = current.split(";", 2)[1];
            found.put(slot, ItemStack.deserialize(deserializeMap(current)));
            current = current.split("]", 2)[0];
        }
        System.out.println("Found: " + found);
        return found;
    }

    private static Map<String, Object> deserializeMap(String current) {
        Map<String, Object> itemData = new HashMap<>();
        while (!current.startsWith("}") && !current.startsWith("]")) {
            String[] split = current.split("=", 2);
            if (split[1].startsWith("{")) {
                itemData.put(split[0], deserializeMap(split[1]));
            } else {
                itemData.put(split[0], split[1].substring(1).split("\\|;", 2)[0].replace("\uD83E\uDED9", "|"));
            }
            current = current.split(";", 2)[1];
        }
        return itemData;
    }

    public static String serialize(Map<Integer, ItemStack> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, ItemStack> entry : data.entrySet()) {
            builder.append("[").append(entry.getKey()).append(";");
            for (Map.Entry<String, Object> itemEntry : entry.getValue().serialize().entrySet()) {
                serialize(builder, itemEntry.getKey(), itemEntry.getValue());
            }
            builder.append("]");
        }
        System.out.println("Serialized to " + builder.toString());
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    private static void serialize(StringBuilder builder, String key, Object value) {
        builder.append(key).append("=");
        if (value instanceof Map) {
            builder.append("{");
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                serialize(builder, entry.getKey(), entry.getValue());
            }
            builder.append("}");
        } else {
            builder.append("|").append(value.toString().replace("|", "\uD83E\uDED9")).append("|");
        }
        builder.append(";");
    }
}
