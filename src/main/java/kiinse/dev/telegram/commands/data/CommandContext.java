package kiinse.dev.telegram.commands.data;

import lombok.Builder;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Builder
public class CommandContext {
    public @NonNull Long chatId;
    public @NonNull User user;
    public @NonNull Message message;
}
