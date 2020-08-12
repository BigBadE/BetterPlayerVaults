package software.bigbade.playervaults.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

public class JsonSerialization extends FileConfiguration {
  public static final String BLANK_CONFIG = "{}\n";

  @SuppressWarnings("unchecked")
  private static JsonElement serialize(Object value) {
    if (value instanceof Object[]) {
      JsonArray array = new JsonArray();
      for (Object object : (Object[])value) {
        array.add(serialize(object));
      }
      return array;
    } else if (value instanceof Iterable<?>) {
      JsonArray array = new JsonArray();
      for (Object object : (Iterable<?>)value) {
        array.add(serialize(object));
      }
      return array;
    } else if (value instanceof Map) {
      JsonObject object = new JsonObject();
      for (Map.Entry<Object, Object> entry :
           ((Map<Object, Object>)value).entrySet()) {
        object.add(entry.getKey().toString(), serialize(entry.getValue()));
      }
      return object;
    } else if (value instanceof ConfigurationSection) {
      JsonObject object = new JsonObject();
      for (Map.Entry<String, Object> entry :
           ((ConfigurationSection)value).getValues(false).entrySet()) {
        object.add(entry.getKey(), serialize(entry.getValue()));
      }
      return object;
    } else if (value instanceof ConfigurationSerializable) {
      ConfigurationSerializable serializable = (ConfigurationSerializable)value;
      Map<String, Object> values = new LinkedHashMap<>();
      if (serializable.getClass() != ItemStack.class) {
        values.put(
            ConfigurationSerialization.SERIALIZED_TYPE_KEY,
            ConfigurationSerialization.getAlias(serializable.getClass()));
      }
      values.putAll(serializable.serialize());
      JsonObject object = new JsonObject();
      for (Map.Entry<String, Object> entry : values.entrySet()) {
        object.add(entry.getKey(), serialize(entry.getValue()));
      }
      return object;
    } else {
      if (value instanceof Number) {
        for (NumberTypes types : NumberTypes.values()) {
          if (value.getClass().equals(types.getClazz())) {
            return new JsonPrimitive(types.getCharacter() + value.toString());
          }
        }
      }
      return new JsonPrimitive(value.toString());
    }
  }

  @Override
  public @Nonnull String saveToString() {
    String output = serialize(getValues(false)).toString();
    if (output.equals(BLANK_CONFIG)) {
      output = "";
    }
    return output;
  }

  @Override
  public void loadFromString(@Nonnull String data) {
    if (data.isEmpty()) {
      return;
    }

    JsonObject object = new JsonParser().parse(data).getAsJsonObject();
    loadData(object, this);
  }

  private void loadData(JsonObject object, ConfigurationSection section) {
    for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
      section.set(entry.getKey(), deserialize(entry.getValue()));
    }
  }

  private Object deserialize(JsonElement element) {
    if (element instanceof JsonObject) {
      JsonObject object = (JsonObject)element;
      Map<String, Object> map = new HashMap<>();
      for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
        map.put(entry.getKey(), deserialize(entry.getValue()));
      }
      if (map.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
        return ConfigurationSerialization.deserializeObject(map);
      }
      return map;
    } else if (element instanceof JsonArray) {
      List<Object> list = new ArrayList<>();
      for (JsonElement listElement : ((JsonArray)element)) {
        list.add(deserialize(listElement));
      }
      return list;
    } else if (element instanceof JsonPrimitive) {
      String value = element.getAsString();
      if (value.isEmpty()) {
        return "";
      }
      String number = value.substring(1);
      if (StringUtils.isNumeric(number)) {
        char character = value.charAt(0);
        for (NumberTypes types : NumberTypes.values()) {
          if (types.getCharacter() == character) {
            return types.getConverter().apply(number);
          }
        }
      }
      return value;
    }
    throw new IllegalStateException("JsonElement has bad class: " +
                                    element.getClass());
  }

  @Override
  protected @Nonnull String buildHeader() {
    return "";
  }
}
