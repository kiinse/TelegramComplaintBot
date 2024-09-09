package kiinse.dev.telegram.commands.command;

import kiinse.dev.telegram.commands.annotations.Command;
import kiinse.dev.telegram.commands.data.CommandContext;
import kiinse.dev.telegram.commands.interfaces.TelegramCommand;
import kiinse.dev.telegram.complaint.Complaint;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Command(
        command = "/viewAdminComplaint",
        aliases = {"Просмотреть жалобы"}
)
public class ViewComplaintAdminCommand extends TelegramCommand {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UsersManager usersManager;
    private final ComplaintManager complaintManager;

    public ViewComplaintAdminCommand(@NonNull UsersManager usersManager, @NonNull ComplaintManager complaintManager) {
        this.usersManager = usersManager;
        this.complaintManager = complaintManager;
    }

    public void onCommand(@NonNull CommandContext context, @NonNull OkHttpTelegramClient client, @NonNull Config config) {
        try {
            if (config.isDebug) {
                logger.info("Executing {} to {}", this.getClass().getSimpleName(),  context.chatId);
            }
            val chatId = context.chatId;
            val userPermissions = usersManager.getUserPermissions(chatId);
            switch (userPermissions) {
                case USER -> new MessageBuilder(client)
                        .send(context.message, config.get("no_permission", "no_permission"));
                case DISTRICT_HEAD -> {
                    val district = usersManager.getDistrict(chatId);
                    val complaints = complaintManager.getComplaints(district);
                    sendAdminComplaints(context, client, complaints);
                }
                case GOVERNOR -> {
                    val complaints = complaintManager.getComplaints();
                    sendAdminComplaints(context, client, complaints);
                }
            }
        } catch (Exception e) {
            logger.error("Error on {} command", this.getClass().getSimpleName(),  e);
        }
    }

    private void sendAdminComplaints(@NonNull CommandContext context, @NonNull OkHttpTelegramClient client, Set<Complaint> complaints) throws TelegramApiException {
        for (val complaint : complaints) {
            new MessageBuilder(client)
                    .send(context.message,
                            complaint.asString(),
                            new KeyboardBuilder().getKeyboard(getAdminKeyboardButtons()));
        }
    }

    private List<String> getAdminKeyboardButtons() {
        return List.of("Просмотреть жалобы");
    }
}
