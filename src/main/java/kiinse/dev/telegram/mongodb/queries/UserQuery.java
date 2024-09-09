package kiinse.dev.telegram.mongodb.queries;

import com.mongodb.client.MongoCollection;
import kiinse.dev.telegram.enums.UserPermission;
import kiinse.dev.telegram.mongodb.MongoDB;
import lombok.NonNull;
import lombok.val;
import org.bson.Document;

public class UserQuery {

    private final MongoDB mongoDB;
    private MongoCollection<Document> collection;

    public UserQuery(@NonNull MongoDB mongoDB) {
        this.mongoDB = mongoDB;
        reload();
    }

    public void reload() {
        collection = mongoDB.getMongoDatabase().getCollection("users");
    }

    public UserPermission getUserPermission(long chatId) {
        if (hasUser(chatId)) {
            val query = new Document();
            query.put("_id", String.valueOf(chatId));
            val result = collection.find(query).first();
            assert result != null;
            return UserPermission.valueOf(result.get("permission", "USER"));
        } else {
            return UserPermission.USER;
        }
    }

    public @NonNull String getUserDistrict(long chatId) {
        if (hasUser(chatId)) {
            val query = new Document();
            query.put("_id", String.valueOf(chatId));
            val result = collection.find(query).first();
            assert result != null;
            return result.get("district", "");
        } else {
            return "";
        }
    }

    public boolean isUserDistrictSelected(long chatId) {
        return !getUserDistrict(chatId).isBlank();
    }

    public void createUser(long chatId) {
        if (!hasUser(chatId)) {
            val query = new Document();
            query.put("_id", String.valueOf(chatId));
            query.put("district", "");
            query.put("permission", UserPermission.USER.toString());
            collection.insertOne(query);
        }
    }

    public void setUserDistrict(long chatId, @NonNull String district) {
        removeUserDistrict(chatId);
        val query = new Document();
        query.put("_id", String.valueOf(chatId));
        query.put("district", district);
        query.put("permission", UserPermission.USER.toString());
        collection.insertOne(query);
    }

    public void setUserPermission(long chatId, UserPermission permission) {
        val district = getUserDistrict(chatId);
        removeUserDistrict(chatId);
        val query = new Document();
        query.put("_id", String.valueOf(chatId));
        query.put("district", district);
        query.put("permission", permission.toString());
        collection.insertOne(query);
    }

    public void removeUserDistrict(long chatId) {
        val query = new Document();
        query.put("_id", String.valueOf(chatId));
        collection.deleteMany(query);
    }

    private boolean hasUser(long chatId) {
        val query = new Document();
        query.put("_id", String.valueOf(chatId));
        val result = collection.find(query).first();
        return result != null && !result.isEmpty();
    }
}
