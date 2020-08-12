package software.bigbade.playervaults;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import software.bigbade.playervaults.loading.DatabaseSettings;
import software.bigbade.playervaults.loading.ExternalDataLoader;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Level;

public class MongoVaultLoader extends ExternalDataLoader {
    private MongoCollection<Document> collection;
    private final MongoClient client;

    public MongoVaultLoader(DatabaseSettings settings) {
        MongoClientSettings.Builder builder = MongoClientSettings.builder().applyConnectionString(new ConnectionString(settings.getUrl())).applicationName("BetterPlayerVaults");
        switch (settings.getSecurity().toUpperCase()) {
            case "SHA256":
                builder.credential(MongoCredential.createScramSha256Credential(settings.getUsername(), settings.getDatabase(), settings.getPassword().toCharArray()));
                break;
            case "SHA1":
                builder.credential(MongoCredential.createScramSha1Credential(settings.getUsername(), settings.getDatabase(), settings.getPassword().toCharArray()));
                break;
            case "PLAIN":
                builder.credential(MongoCredential.createPlainCredential(settings.getUsername(), settings.getDatabase(), settings.getPassword().toCharArray()));
                PlayerVaults.getPluginLogger().log(Level.WARNING, "PLAIN VERIFICATION IS ENABLED. THIS IS A SERIOUS SECURITY BREACH!");
                break;
            case "X509":
                builder.credential(MongoCredential.createMongoX509Credential());
                break;
            case "DEFAULT":
            default:
                builder.credential(MongoCredential.createCredential(settings.getUsername(), settings.getDatabase(), settings.getPassword().toCharArray()));
        }
        client = MongoClients.create(builder.build());
        collection = client.getDatabase(settings.getDatabase()).getCollection(settings.getTableName());

        if (collection == null) {
            PlayerVaults.getPluginLogger().log(Level.INFO, "Creating new database section");
            client.getDatabase(settings.getDatabase()).createCollection(settings.getTableName());
            collection = client.getDatabase(settings.getDatabase()).getCollection(settings.getTableName());
        }
    }

    @Override
    public byte[] loadData(UUID player, int vault) {
        Document document = collection.find(Filters.and(Filters.eq("uuid", player.toString()), Filters.eq("id", vault))).first();
        if (document == null) {
            return new byte[0];
        }
        return document.getString("inventory").getBytes(StandardCharsets.ISO_8859_1);
    }

    @Override
    public void saveData(UUID player, int number, byte[] data) {
        collection.updateOne(
                Filters.and(Filters.eq("uuid", player),
                        Filters.eq("id", number)),
                Updates.combine(Updates.set("inventory", new String(data, StandardCharsets.ISO_8859_1))));
    }

    @Override
    public String getName() {
        return "mongo";
    }

    @Override
    public void resetVault(UUID player, int number) {
        collection.deleteOne(Filters.and(Filters.eq("uuid", player), Filters.eq("id", number)));
    }

    @Override
    public boolean worksOffline() {
        return true;
    }

    @Override
    public void shutdown() {
        client.close();
    }
}
