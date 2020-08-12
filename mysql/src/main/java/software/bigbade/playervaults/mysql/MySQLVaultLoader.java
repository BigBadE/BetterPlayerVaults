package software.bigbade.playervaults.mysql;

import software.bigbade.playervaults.loading.DatabaseSettings;
import software.bigbade.playervaults.loading.ExternalDataLoader;
import software.bigbade.playervaults.mysql.utils.MySQLUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class MySQLVaultLoader extends ExternalDataLoader {
    private static final Pattern NAME_PATTERN = Pattern.compile("[^a-zA-Z_\\d]");

    private final DatabaseSettings settings;

    private PreparedStatement updateVaultStatement;
    private PreparedStatement getVaultStatement;
    private Connection connection;

    public MySQLVaultLoader(DatabaseSettings settings) {
        this.settings = settings;
        try {
            connection = DriverManager.getConnection("jdbc:" + settings.getUrl(),
                    settings.getUsername(),
                    settings.getPassword());
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Error loading database (check the URL!)",
                    e);
            return;
        }

        getDatabase();
        String tableName = createTable();
        getVaultStatement = MySQLUtils.getStatement(
                connection, "SELECT inventory FROM " + tableName +
                        " WHERE uuid=? AND id=? LIMIT 1;");
        updateVaultStatement = MySQLUtils.getStatement(
                connection,
                "INSERT INTO " + tableName +
                        "(uuid, id, inventory) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE inventory=VALUES(inventory);");
    }

    @SuppressWarnings("DuplicatedCode")
    private void getDatabase() {
        String database = settings.getDatabase();
        if (NAME_PATTERN.matcher(database).matches()) {
            getLogger().log(
                    Level.SEVERE,
                    "ILLEGAL CHARACTER DETECTED IN DATABASE NAME. POSSIBLE MYSQL INJECTION!");
            database = "better_player_vaults";
        }
        PreparedStatement statement = MySQLUtils.getStatement(
                connection, "CREATE DATABASE IF NOT EXISTS " + database);
        if (statement != null) {
            MySQLUtils.executeUpdate(statement);
            MySQLUtils.safeClose(statement);
        }
        statement = MySQLUtils.getStatement(connection, "USE " + database);
        if (statement != null && !MySQLUtils.isClosed(statement)) {
            MySQLUtils.executeUpdate(statement);
            MySQLUtils.safeClose(statement);
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private String createTable() {
        String tableName = settings.getTableName();
        if (NAME_PATTERN.matcher(tableName).matches()) {
            getLogger().log(
                    Level.SEVERE,
                    "ILLEGAL CHARACTER DETECTED IN TABLE NAME. POSSIBLE MYSQL INJECTION!");
            tableName = "players";
            return tableName;
        }
        ResultSet resultSet = null;
        try (
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM information_schema.TABLES WHERE table_schema = '" + settings.getUsername() + "' AND table_name = '" + tableName + "' LIMIT 1;")) {
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                try (
                        PreparedStatement createTable = connection.prepareStatement(
                                "CREATE TABLE " + tableName +
                                        "(uuid CHAR(36) NOT NULL PRIMARY KEY, number INT NOT NULL, inventory TEXT NOT NULL);")) {
                    getLogger().log(Level.INFO, "Creating vault table!");
                    createTable.executeUpdate();
                }
            }
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Error getting/creating table", e);
        } finally {
            if (resultSet != null) {
                MySQLUtils.safeClose(resultSet);
            }
        }
        return tableName;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public byte[] loadData(UUID player, int vault) {
        MySQLUtils.setString(getVaultStatement, player.toString());
        MySQLUtils.setInt(getVaultStatement, vault);
        ResultSet result = MySQLUtils.getResults(getVaultStatement);
        if (result == null || !MySQLUtils.next(result)) {
            return new byte[0];
        }
        byte[] data = MySQLUtils.getBytes(result);
        MySQLUtils.safeClose(result);
        return data;
    }

    @Override
    public void saveData(UUID player, int number, byte[] data) {
        MySQLUtils.setString(updateVaultStatement, player.toString());
        MySQLUtils.setInt(updateVaultStatement, number);
        MySQLUtils.setBytes(updateVaultStatement, data);
        MySQLUtils.executeUpdate(updateVaultStatement);
    }

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public void resetVault(UUID player, int number) {
        MySQLUtils.setString(updateVaultStatement, player.toString());
        MySQLUtils.setInt(updateVaultStatement, number);
        MySQLUtils.setBytes(updateVaultStatement, new byte[0]);
        MySQLUtils.executeUpdate(updateVaultStatement);
    }

    @Override
    public boolean worksOffline() {
        return true;
    }

    @Override
    public void shutdown() {
        MySQLUtils.safeClose(getVaultStatement);
        MySQLUtils.safeClose(updateVaultStatement);
    }
}
