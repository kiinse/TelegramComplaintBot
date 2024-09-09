package kiinse.dev.telegram.mongodb.builder;

import lombok.Builder;
import lombok.NonNull;

@Builder
public class DBSettings {
    public @NonNull String host;
    public @NonNull String port;
    public @NonNull String login;
    public @NonNull String password;
    public @NonNull String dbName;
    public @NonNull String authDb;
}
