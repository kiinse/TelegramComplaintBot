package kiinse.dev.telegram.mongodb.db;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import kiinse.dev.telegram.mongodb.builder.DBSettings;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class DBConnection {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DBSettings settings;
    @Getter
    private MongoClient mongoClient;
    @Getter
    private MongoDatabase mongoDatabase;

    public DBConnection(@NonNull DBSettings settings) {
        this.settings = settings;
    }

    public void connect() {
        try {
            logger.info("Connecting to database...");
            val credentials = MongoCredential.createCredential(
                    settings.login,
                    settings.authDb,
                    settings.password.toCharArray()
            );
            ServerAddress serverAddress;
            if (settings.port.isBlank()) {
                serverAddress = new ServerAddress(settings.host);
            } else {
                serverAddress = new ServerAddress(settings.host, Integer.parseInt(settings.port));
            }
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder -> builder.hosts(
                                    List.of(serverAddress)
                            ))
                            .credential(credentials)
                            .build()
            );
            mongoDatabase = mongoClient.getDatabase(settings.dbName);
            createTables();

            logger.info("Database connected!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected abstract void createTables();

    protected void createCollectionIfNotExists(String collection) {
        if (hasCollection(collection)) { return; }
        mongoDatabase.createCollection(collection);
        logger.info("Collection '{}' has been created!", collection);
    }

    protected boolean hasCollection(String collection) {
        for (val col : mongoDatabase.listCollectionNames()) {
            if (col.equals(collection)) {
                return true;
            }
        }
        return false;
    }
}
