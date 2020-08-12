package software.bigbade.playervaults.serialization;

import be.seeseemelk.mockbukkit.MockBukkit;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.material.Colorable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SerializationTest {
  @Before
  public void setUp() {
    MockBukkit.mock();
  }

  @After
  public void tearDown() {
    MockBukkit.unmock();
  }

  @Test
  public void serializationTest() {
    Inventory inventory = Bukkit.createInventory(null, 27, "TestInv");
    inventory.setItem(0, new ItemBuilder(Material.LEATHER_BOOTS)
                             .setColor(DyeColor.BLUE)
                             .build());
    inventory.setItem(
        1, new ItemBuilder(Material.DIAMOND_SWORD).setDamage(123).build());
    Assert.assertEquals(
        inventory, SerializationUtils.deserialize(
                       SerializationUtils.serialize(inventory), "TestInv", 27));
  }
}

class ItemBuilder {
  private final ItemStack item;

  public ItemBuilder(Material material) { item = new ItemStack(material); }

  public ItemBuilder setColor(DyeColor color) {
    ((Colorable)Objects.requireNonNull(item.getItemMeta())).setColor(color);
    return this;
  }

  public ItemBuilder setDamage(int damage) {
    ((Damageable)Objects.requireNonNull(item.getItemMeta())).setDamage(damage);
    return this;
  }

  public ItemStack build() { return item; }
}