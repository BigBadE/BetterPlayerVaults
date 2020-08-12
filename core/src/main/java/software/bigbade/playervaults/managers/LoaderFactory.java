package software.bigbade.playervaults.managers;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import software.bigbade.playervaults.BetterPlayerVaults;
import software.bigbade.playervaults.MongoVaultLoader;
import software.bigbade.playervaults.api.IVaultLoader;
import software.bigbade.playervaults.loading.DatabaseSettings;
import software.bigbade.playervaults.loading.FlatfileVaultLoader;
import software.bigbade.playervaults.loading.MySQLVaultLoader;
import software.bigbade.playervaults.loading.PersistentVaultLoader;

@RequiredArgsConstructor
public class LoaderFactory {
    private final BetterPlayerVaults main;

    public boolean checkName(String name, int version) {
        return name.equals("flatfile") || name.equals("mysql") || name.equals("mongo") || (version >= 14 && name.equals("persistent"));
    }

    public IVaultLoader getVaultLoader(String loader) {
        DatabaseSettings.DatabaseSettingsBuilder builder = DatabaseSettings.builder();
        ConfigurationSection section = main.getConfig().getConfigurationSection("database");
        if(section == null) {
            section = main.getConfig().createSection("database");
        }
        builder.url(section.getString("url", "mysql://localhost:3306"));
        builder.database(section.getString("database", "better_player_vaults"));
        builder.username(section.getString("username", "root"));
        builder.password(section.getString("password", ""));
        builder.tableName(section.getString("table-name", "better_player_vaults"));
        builder.security(section.getString("encryption", "DEFAULT"));

        switch (loader) {
            case "persistent":
                return new PersistentVaultLoader(main);
            case "flatfile":
                return new FlatfileVaultLoader(main.getDataFolder().getAbsolutePath());
            case "mysql":
                return new MySQLVaultLoader(builder.build());
            case "mongo":
                return new MongoVaultLoader(builder.build());
            default:
                throw new IllegalStateException("checkName passed, but loader not found!");
        }
    }
}
