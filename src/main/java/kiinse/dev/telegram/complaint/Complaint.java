package kiinse.dev.telegram.complaint;

import lombok.Builder;
import lombok.NonNull;

@Builder
public class Complaint {
    public @NonNull String id;
    public @NonNull Long chatId;
    public @NonNull String username;
    public @NonNull String district;
    public @NonNull String date;
    public @NonNull String text;

    public @NonNull String asString() {
        return "Район: " + district + ", Дата: " + date + "\nПользователь: " + username + "\n-----------\n" + text;
    }
}
