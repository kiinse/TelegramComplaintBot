package kiinse.dev.telegram.commands.command;

import kiinse.dev.telegram.commands.annotations.Command;
import kiinse.dev.telegram.commands.data.CommandContext;
import kiinse.dev.telegram.commands.interfaces.TelegramCommand;
import kiinse.dev.telegram.configuration.Config;
import kiinse.dev.telegram.manager.ComplaintManager;
import kiinse.dev.telegram.manager.UsersManager;
import kiinse.dev.telegram.utils.KeyboardBuilder;
import kiinse.dev.telegram.utils.MessageBuilder;
import lombok.NonNull;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Command(
        command = "/viewComplaint",
        aliases = {"Просмотреть жалобу"}
)
public class ViewComplaintCommand extends TelegramCommand {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UsersManager usersManager;
    private final ComplaintManager complaintManager;

    public ViewComplaintCommand(@NonNull UsersManager usersManager, @NonNull ComplaintManager complaintManager) {
        this.usersManager = usersManager;
        this.complaintManager = complaintManager;
    }

    public void onCommand(@NonNull CommandContext context, @NonNull OkHttpTelegramClient client, @NonNull Config config) {
        try {
            if (config.isDebug) {
                logger.info("Executing {} to {}", this.getClass().getSimpleName(),  context.chatId);
            }
            long chatId = context.chatId;
            val complaint = complaintManager.getComplaint(chatId);
            if (complaint == null || complaint.text.isBlank()) {
                new MessageBuilder(client)
                        .send(context.message,
                                config.get("no_complaint", "no_complaint"),
                                new KeyboardBuilder().getKeyboard(getKeyboardButtons()));
            } else {
                new MessageBuilder(client)
                        .send(context.message,
                                config.get("complaint_view", "complaint_view"));
                new MessageBuilder(client)
                        .send(context.message,
                                "---Жалоба---\n" + complaintManager.getComplaint(chatId),
                                new KeyboardBuilder().getKeyboard(getKeyboardButtons()),
                                complaint);
            }

        } catch (Exception e) {
            logger.error("Error on {} command", this.getClass().getSimpleName(),  e);
        }
    }

    private @NonNull List<String> getKeyboardButtons() {
        return Arrays.asList(
                "Отправить жалобу",
                "Удалить жалобу",
                "Сменить район"
        );
    }
}
