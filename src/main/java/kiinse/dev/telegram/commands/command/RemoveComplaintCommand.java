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

import java.util.Arrays;
import java.util.List;

@Command(
        command = "/removeComplaint",
        aliases = {"Удалить жалобу"}
)
public class RemoveComplaintCommand extends TelegramCommand {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UsersManager usersManager;
    private final ComplaintManager complaintManager;

    public RemoveComplaintCommand(@NonNull UsersManager usersManager, @NonNull ComplaintManager complaintManager) {
        this.usersManager = usersManager;
        this.complaintManager = complaintManager;
    }

    public void onCommand(@NonNull CommandContext context, @NonNull OkHttpTelegramClient client, @NonNull Config config) {
        try {
            if (config.isDebug) {
                logger.info("Executing {} to {}", this.getClass().getSimpleName(),  context.chatId);
            }

            val chatId = context.chatId;
            val complaint = complaintManager.getComplaint(context.chatId);
            if (complaint == null || complaint.text.isBlank()) {
                new MessageBuilder(client)
                        .send(context.message,
                                config.get("no_complaint", "no_complaint"),
                                new KeyboardBuilder().getKeyboard(getKeyboardButtons()));
            } else {
                complaintManager.removeComplaint(chatId);
                new MessageBuilder(client)
                        .send(context.message,
                                config.get("complaint_removed", "complaint_removed"),
                                new KeyboardBuilder().getKeyboard(getKeyboardButtons()));
            }

        } catch (Exception e) {
            logger.error("Error on {} command", this.getClass().getSimpleName(),  e);
        }
    }

    private List<String> getKeyboardButtons() {
        return Arrays.asList(
                "Отправить жалобу",
                "Сменить район"
        );
    }
}
