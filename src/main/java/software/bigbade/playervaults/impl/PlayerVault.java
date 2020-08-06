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

    private boolean opened = true;

    @Override
    public void toggleClosed() {
        opened = !opened;
    }
}
