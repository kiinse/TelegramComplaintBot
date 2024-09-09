package kiinse.dev.telegram.manager;

import kiinse.dev.telegram.complaint.Complaint;
import kiinse.dev.telegram.mongodb.queries.ComplaintQuery;
import lombok.NonNull;
import lombok.val;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Set;

public class ComplaintManager {

    private final ComplaintQuery complaintQuery;
    private final HashMap<Long, String> userComplaints = new HashMap<>();
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ComplaintManager(@NonNull ComplaintQuery complaintQuery) {
        this.complaintQuery = complaintQuery;
    }

    public void removeComplaint(long chatId) {
        userComplaints.remove(chatId);
    }

    public void addComplaint(long chatId, @NonNull String text) {
        String finalText;
        if (!userComplaints.containsKey(chatId)) {
            finalText = text;
        } else {
            finalText = userComplaints.get(chatId) + "\n" + text;
        }
        userComplaints.put(chatId, finalText);
    }

    public @NonNull String getComplaint(long chatId) {
        if (!userComplaints.containsKey(chatId)) {
            return "";
        }
        return userComplaints.get(chatId);
    }

    public boolean hasComplaint(long chatId) {
        if (!userComplaints.containsKey(chatId)) {
            return false;
        }
        return !userComplaints.get(chatId).isBlank();
    }

    public void sendComplaint(long chatId, @NonNull String userName, @NonNull String district) {
        complaintQuery.createComplaint(Complaint.builder()
                .chatId(chatId)
                .username(userName)
                .district(district)
                .date(LocalDate.now().format(dateFormat))
                .text(getComplaint(chatId))
                .build());
    }

    public @NonNull Set<Complaint> getComplaints(@NonNull String district) {
        return complaintQuery.getComplaints(district);
    }

    public @NonNull Set<Complaint> getComplaints() {
        return complaintQuery.getComplaints();
    }
}
