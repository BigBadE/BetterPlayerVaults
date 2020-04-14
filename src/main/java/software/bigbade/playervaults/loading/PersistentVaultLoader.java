package software.bigbade.playervaults.loading;

import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import software.bigbade.playervaults.BetterPlayerVaults;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.serialization.SerializationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class PersistentVaultLoader implements IVaultLoader {
    private final List<NamespacedKey> keys = new ArrayList<>();

    private final BetterPlayerVaults vaults;

    @Override
    public Map<Integer, ItemStack> getVault(Player player, int vault) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey key = getKey(vault);
        if (!data.has(key, PersistentDataType.STRING))
            return new HashMap<>();
        String serialized = data.get(keys.get(vault), PersistentDataType.STRING);
        Objects.requireNonNull(serialized);
        return SerializationUtils.deserialize(serialized);
    }

    @Override
    public void saveVault(IPlayerVault vault) {
        Map<Integer, ItemStack> saving = new HashMap<>();
        for (int i = 0; i < vault.getInventory().getSize(); i++) {
            ItemStack found = vault.getInventory().getItem(i);
            if (found != null) {
                saving.put(i, found);
            }
        }
        PersistentDataContainer data = vault.getPlayer().getPersistentDataContainer();
        String serialized = SerializationUtils.serialize(saving);
        data.set(getKey(vault.getNumber()), PersistentDataType.STRING, serialized);
    }

    @Override
    public String getName() {
        return "persistent";
    }

    private NamespacedKey getKey(int slot) {
        if (keys.size() < slot) {
            for (int i = keys.size(); i <= slot; i++) {
                keys.add(new NamespacedKey(vaults, "vault" + i));
            }
        }
        return keys.get(slot);
    }
}
