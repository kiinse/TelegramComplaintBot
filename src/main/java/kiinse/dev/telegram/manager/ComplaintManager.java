package kiinse.dev.telegram.manager;

import kiinse.dev.telegram.complaint.Complaint;
import kiinse.dev.telegram.mongodb.queries.ComplaintQuery;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ComplaintManager {

    private final ComplaintQuery complaintQuery;
    private final HashMap<Long, Complaint> userComplaints = new HashMap<>();
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ComplaintManager(@NonNull ComplaintQuery complaintQuery) {
        this.complaintQuery = complaintQuery;
    }

    public void removeComplaint(long chatId) {
        userComplaints.remove(chatId);
    }

    public @NonNull Complaint addComplaint(long chatId, String username, String district, @NonNull String text) {
        var complaint = userComplaints.get(chatId);
        if (complaint == null) {
            complaint = Complaint.builder()
                    .id(UUID.randomUUID().toString() + LocalDate.now())
                    .chatId(chatId)
                    .username(username)
                    .district(district)
                    .date(LocalDate.now().format(dateFormat))
                    .text("")
                    .build();
        }
        if (!text.isBlank()) {
            if (complaint.text.isBlank()) {
                complaint.text = text;
            } else {
                complaint.text = complaint.text + "\n" + text;
            }
            userComplaints.put(chatId, complaint);
        }
        return complaint;
    }

    public @Nullable Complaint getComplaint(long chatId) {
        return userComplaints.get(chatId);
    }

    public boolean hasComplaint(long chatId) {
        if (!userComplaints.containsKey(chatId)) {
            return false;
        }
        return !userComplaints.get(chatId).text.isBlank();
    }

    public void sendComplaint(long chatId) {
        if (hasComplaint(chatId)) {
            complaintQuery.createComplaint(Objects.requireNonNull(getComplaint(chatId)));
        }
    }

    public @NonNull Set<Complaint> getComplaints(@NonNull String district) {
        return complaintQuery.getComplaints(district);
    }

    public @NonNull Set<Complaint> getComplaints() {
        return complaintQuery.getComplaints();
    }
}
