package software.bigbade.playervaults.loading;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import software.bigbade.playervaults.BetterPlayerVaults;
import software.bigbade.playervaults.api.IPlayerVault;
import software.bigbade.playervaults.serialization.SerializationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class PersistentVaultLoader implements IVaultLoader {
    private final List<NamespacedKey> keys = new ArrayList<>();

    private final BetterPlayerVaults vaults;

    @Override
    public Inventory getVault(Player player, int vault) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey key = getKey(vault);
        if (!data.has(key, PersistentDataType.STRING)) {
            return Bukkit.createInventory(null, 27, "Vault " + vault);
        }
        String serialized = data.get(keys.get(vault), PersistentDataType.STRING);
        Objects.requireNonNull(serialized);
        return SerializationUtils.deserialize(serialized);
    }

    @Override
    public void saveVault(IPlayerVault vault) {
        PersistentDataContainer data = vault.getPlayer().getPersistentDataContainer();
        data.set(getKey(vault.getNumber()), PersistentDataType.STRING, SerializationUtils.serialize(vault.getInventory(), "Vault " + vault.getNumber()));
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
