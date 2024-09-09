package kiinse.dev.telegram.mongodb.queries;

import com.mongodb.client.MongoCollection;
import kiinse.dev.telegram.complaint.Complaint;
import kiinse.dev.telegram.mongodb.MongoDB;
import lombok.NonNull;
import lombok.val;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;

public class ComplaintQuery {

    private MongoDB mongoDB;
    private MongoCollection<Document> collection;

    public ComplaintQuery(@NonNull MongoDB mongoDB) {
        this.mongoDB = mongoDB;
        reload();
    }

    public void reload() {
        collection = mongoDB.getMongoDatabase().getCollection("complaints");
    }

    public void createComplaint(@NonNull Complaint complaint) {
        val query = new Document();
        query.put("_id", complaint.id);
        query.put("chatId", String.valueOf(complaint.chatId));
        query.put("district", complaint.district);
        query.put("username", complaint.username);
        query.put("date", complaint.date);
        query.put("text", complaint.text);
        collection.insertOne(query);
    }

    public @NonNull Set<Complaint> getComplaints() {
        val set = new HashSet<Complaint>();
        val documents = collection.find();
        for (val document : documents) {
            if (document != null && !document.isEmpty()) {
                set.add(documentToComplain(document));
            }
        }
        return set;
    }

    public @NonNull Set<Complaint> getComplaints(@NonNull String district) {
        val dst = district.toLowerCase();
        val set = new HashSet<Complaint>();
        val query = new Document();
        query.put("district", dst);
        val documents = collection.find(query);
        for (val document : documents) {
            if (document != null && !document.isEmpty()) {
                set.add(documentToComplain(document));
            }
        }
        return set;
    }

    public @NonNull Complaint documentToComplain(@NonNull Document document) {
        return Complaint.builder()
                .id(document.get("_id", ""))
                .chatId(Long.parseLong(document.get("chatId", "0")))
                .username(document.get("username", ""))
                .district(document.get("district", ""))
                .date(document.get("date", ""))
                .text(document.get("text", ""))
                .build();
    }
}
