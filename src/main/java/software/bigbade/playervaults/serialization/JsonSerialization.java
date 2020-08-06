package software.bigbade.playervaults.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import software.bigbade.playervaults.utils.CompressionUtil;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonSerialization extends FileConfiguration {
    private static final String BLANK_CONFIG = "{}\n";

    @Override
    public @Nonnull String saveToString() {
        String output = new String(CompressionUtil.compress(serialize(getValues(false)).toString()), StandardCharsets.UTF_8);

        if (output.equals(BLANK_CONFIG)) {
            output = "";
        }
        return output;
    }

    @Override
    public void loadFromString(@Nonnull String data) {
        if(data.isEmpty()) {
            return;
        }

        Map<Object, Object> dataMap = new Gson().fromJson(CompressionUtil.decompress(data), Map.class);
        System.out.println(printData(dataMap));
        for(Map.Entry<Object, Object> entry : dataMap.entrySet()) {
            set(entry.getKey().toString(), entry.getValue());
        }
    }

    private String printData(Object object) {
        if(object instanceof Map) {
            StringBuilder builder = new StringBuilder();
            ((Map<Object, Object>) object).forEach((key, value) -> builder.append(key.toString()).append(": ").append(printData(value)));
            return builder.toString();
        } else if(object instanceof Iterable) {
            StringBuilder builder = new StringBuilder();
            for(Object value : (Iterable<Object>) object) {
                builder.append(value).append(",");
            }
            builder.substring(0, builder.length());
            return builder.toString();
        } else {
            return object.toString();
        }
    }

    private static JsonElement serialize(Object value) {
        if (value instanceof Object[]) {
            JsonArray array = new JsonArray();
            for (Object object : (Object[]) value) {
                array.add(serialize(object));
            }
            return array;
        } else if (value instanceof Iterable<?>) {
            JsonArray array = new JsonArray();
            for (Object object : (Iterable<?>) value) {
                array.add(serialize(object));
            }
            return array;
        } else if (value instanceof ConfigurationSection) {
            JsonObject object = new JsonObject();
            for (Map.Entry<String, Object> entry : ((ConfigurationSection) value).getValues(false).entrySet()) {
                object.add(entry.getKey(), serialize(entry.getValue()));
            }
            return object;
        } else if (value instanceof ConfigurationSerializable) {
            ConfigurationSerializable serializable = (ConfigurationSerializable) value;
            Map<String, Object> values = new LinkedHashMap<>();
            if (serializable.getClass() != ItemStack.class) {
                values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
            }
            values.putAll(serializable.serialize());
            JsonObject object = new JsonObject();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                object.add(entry.getKey(), serialize(entry.getValue()));
            }
            return object;
        } else {
            return new JsonPrimitive(value.toString());
        }
    }

    @Override
    protected @Nonnull String buildHeader() {
        return "";
    }
}
