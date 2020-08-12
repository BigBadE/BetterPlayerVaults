package software.bigbade.playervaults.serialization;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import software.bigbade.playervaults.utils.CompressionUtil;

public final class SerializationUtils {
  private SerializationUtils() {}

  /**
   * Deserializes given data
   *
   * @param data  Serialized data
   * @param title Inventory title
   * @param size  Inventory size
   * @return Deserialized inventory
   */
  public static Inventory deserialize(byte[] data, String title, int size) {
    return deserialize(CompressionUtil.decompress(data), title, size);
  }

  /**
   * Deserializes given data
   *
   * @param data  Serialized data
   * @param title Inventory title
   * @param size  Inventory size
   * @return Deserialized inventory
   */
  private static Inventory deserialize(String data, String title, int size) {
    Inventory inventory = Bukkit.createInventory(null, size, title);

    JsonSerialization serialization = new JsonSerialization();
    serialization.loadFromString(data);
    for (String key : serialization.getKeys(false)) {
      ItemStack item = serialization.getItemStack(key);
      inventory.setItem(Integer.parseInt(key), item);
    }
    return inventory;
  }

  /**
   * Serializes vault
   *
   * @param inventory Inventory to serialize
   * @return Serialized vault
   */
  public static byte[] serialize(Inventory inventory) {
    JsonSerialization serializer = new JsonSerialization();
    for (int i = 0; i < inventory.getSize(); i++) {
      ItemStack item = inventory.getItem(i);
      if (item == null) {
        continue;
      }
      serializer.set(i + "", item);
    }
    return CompressionUtil.compress(serializer.saveToString());
  }
}
