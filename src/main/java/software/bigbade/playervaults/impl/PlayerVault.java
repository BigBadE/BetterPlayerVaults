package software.bigbade.playervaults.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;

import java.util.UUID;

@RequiredArgsConstructor
public class PlayerVault implements IPlayerVault {
    @Getter
    private final UUID owner;
    @Getter
    private final Inventory inventory;
    @Getter
    private final int number;

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if (IPlayerVault.class.isAssignableFrom(obj.getClass())) {
            return ((IPlayerVault) obj).getOwner().equals(getOwner());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getOwner().hashCode();
    }
}
