package software.bigbade.playervaults.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import software.bigbade.playervaults.api.IPlayerVault;

@RequiredArgsConstructor
public class PlayerVault implements IPlayerVault {
    @Getter
    private final Player player;
    @Getter
    private final Inventory inventory;
    @Getter
    private final int number;
}
