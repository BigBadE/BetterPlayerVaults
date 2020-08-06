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
import software.bigbade.playervaults.managers.VaultManager;
import software.bigbade.playervaults.serialization.SerializationUtils;
import software.bigbade.playervaults.utils.VaultSizeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class PersistentVaultLoader implements IVaultLoader {
    private final List<NamespacedKey> keys = new ArrayList<>();

    private final BetterPlayerVaults vaults;

    @Override
    public Inventory getVault(Player player, int vault) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        NamespacedKey key = getKey(vault);
        if (!data.has(key, PersistentDataType.BYTE_ARRAY)) {
            return Bukkit.createInventory(null, VaultSizeUtil.getSize(player), VaultManager.VAULT_TITLE.translate(vault));
        }
        byte[] serialized = data.get(keys.get(vault), PersistentDataType.BYTE_ARRAY);
        Objects.requireNonNull(serialized);
        return SerializationUtils.deserialize(serialized, VaultManager.VAULT_TITLE.translate(vault), VaultSizeUtil.getSize(player));
    }

    @Override
    public Inventory getVault(UUID player, int vault, int size) {
        throw new UnsupportedOperationException("Cannot open offline player vaults with PersistentData due to Spigot limitations");
    }

    @Override
    public void saveVault(UUID owner, IPlayerVault vault) {
        PersistentDataContainer data = Objects.requireNonNull(Bukkit.getPlayer(owner)).getPersistentDataContainer();
        data.set(getKey(vault.getNumber()), PersistentDataType.BYTE_ARRAY, SerializationUtils.serialize(vault.getInventory()));
    }

    @Override
    public String getName() {
        return "persistent";
    }

    @Override
    public void resetVault(Player player, int number) {
        player.getPersistentDataContainer().remove(getKey(number));
    }

    @Override
    public void resetVault(UUID player, int number) {
        throw new IllegalStateException("PersistentVaultLoader cannot get offline players!");
    }

    @Override
    public boolean worksOffline() {
        return false;
    }

    private NamespacedKey getKey(int slot) {
        if (keys.size() <= slot) {
            for (int i = keys.size(); i <= slot; i++) {
                keys.add(new NamespacedKey(vaults, "vault" + i));
            }
        }
        return keys.get(slot);
    }
}
