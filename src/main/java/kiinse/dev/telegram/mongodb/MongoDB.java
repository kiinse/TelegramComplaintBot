package kiinse.dev.telegram.mongodb;

import kiinse.dev.telegram.mongodb.builder.DBSettings;
import kiinse.dev.telegram.mongodb.db.DBConnection;
import lombok.NonNull;

public class MongoDB extends DBConnection {

    public MongoDB(@NonNull DBSettings settings) {
        super(settings);
        connect();
    }

    @Override
    protected void createTables() {
        createCollectionIfNotExists("users");
        createCollectionIfNotExists("complaints");
    }
}
