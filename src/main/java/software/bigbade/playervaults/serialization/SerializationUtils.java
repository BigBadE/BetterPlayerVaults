package software.bigbade.playervaults.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import software.bigbade.playervaults.utils.CompressionUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SerializationUtils {
    private static final String INDEX = "index";
    private SerializationUtils() { }

    /**
     * Deserializes given data
     *
     * @param data Serialized data
     * @param size Inventory size
     * @return Deserialized inventory
     */
    public static Inventory deserialize(@Nonnull String data, int size) {
        return deserialize(new JsonParser().parse(CompressionUtil.decompress(data)).getAsJsonObject(), size);
    }

    /**
     * Deserializes given data
     *
     * @param data Serialized data
     * @param size Inventory size
     * @return Deserialized inventory
     */
    public static Inventory deserialize(byte[] data, int size) {
        return deserialize(new JsonParser().parse(CompressionUtil.decompress(data)).getAsJsonObject(), size);
    }

    private static Inventory deserialize(JsonObject json, int size) {
        String title = "";
        if(json.has("name")) {
            title = json.get("name").getAsString();
        }
        size = Math.min(Math.max(9, size), 54);
        if(size%9!=0) {
            //Round size to the nearest multiple of 9 using integer division
            size = 9*size/9;
        }
        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (JsonElement element : json.get("items").getAsJsonArray()) {
            JsonObject item = element.getAsJsonObject();
            Map<String, Object> map = new HashMap<>();
            for(Map.Entry<String, JsonElement> entry : item.get("item").getAsJsonObject().entrySet()) {
                if(entry.getKey().equals(INDEX)) {
                    continue;
                }
                map.put(entry.getKey(), deserialize(entry.getValue()));
            }
            ItemStack itemstack = (ItemStack) ConfigurationSerialization.deserializeObject(map);
            inventory.setItem(item.get(INDEX).getAsInt(), itemstack);
        }
        return inventory;
    }

    /**
     * Serializes vault
     *
     * @param inventory Inventory to serialize
     * @return Serialized vault
     */
    public static byte[] serialize(Inventory inventory, String title) {
        JsonObject json = new JsonObject();
        json.addProperty("name", title);
        JsonArray itemArray = new JsonArray();
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if(item == null) {
                continue;
            }
            JsonObject itemObject = new JsonObject();
            itemObject.add("item", serialize(item));
            itemObject.addProperty(INDEX, i);
            itemArray.add(itemObject);
        }
        json.add("items", itemArray);
        return CompressionUtil.compress(json.toString());
    }

    private static JsonElement serialize(Object value) {
        if (value instanceof Object[]) {
            JsonArray array = new JsonArray();
            for(Object object : (Object[]) value) {
                array.add(serialize(object));
            }
            return array;
        } else if (value instanceof Iterable<?>) {
            JsonArray array = new JsonArray();
            for(Object object : (Iterable<?>) value) {
                array.add(serialize(object));
            }
            return array;
        } else if (value instanceof ConfigurationSection) {
            JsonObject object = new JsonObject();
            for(Map.Entry<String, Object> entry : ((ConfigurationSection) value).getValues(false).entrySet()) {
                object.add(entry.getKey(), serialize(entry.getValue()));
            }
            return object;
        } else if (value instanceof ConfigurationSerializable) {
            ConfigurationSerializable serializable = (ConfigurationSerializable) value;
            Map<String, Object> values = new LinkedHashMap<>();
            values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
            values.putAll(serializable.serialize());
            JsonObject object = new JsonObject();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                object.add(entry.getKey(), serialize(entry.getValue()));
            }
            return object;
        } else if(value instanceof Number) {
            return new JsonPrimitive((Number) value);
        } else {
            return new JsonPrimitive(value.toString());
        }
    }

    private static Object deserialize(JsonElement element) {
        if(element instanceof JsonObject) {
            Map<String, Object> values = new HashMap<>();
            for(Map.Entry<String, JsonElement> entry : ((JsonObject) element).entrySet()) {
                values.put(entry.getKey(), deserialize(entry.getValue()));
            }
            return values;
        } else if(element instanceof JsonArray) {
            List<Object> list = new ArrayList<>();
            for(JsonElement listElement : (JsonArray) element) {
                list.add(deserialize(listElement));
            }
            return list;
        } else if(element instanceof JsonPrimitive) {
            JsonPrimitive primitive = (JsonPrimitive) element;
            if(primitive.isNumber()) {
                return primitive.getAsNumber();
            } else if(primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else {
                return primitive.getAsString();
            }
        } else {
            throw new IllegalArgumentException("Unknown JSON type: " + element.getClass());
        }
    }
}
