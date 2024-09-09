package kiinse.dev.telegram.commands.command;

import kiinse.dev.telegram.commands.annotations.Command;
import kiinse.dev.telegram.commands.data.CommandContext;
import kiinse.dev.telegram.commands.interfaces.TelegramCommand;
import kiinse.dev.telegram.configuration.Config;
import kiinse.dev.telegram.enums.UserPermission;
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
import java.util.Objects;

@Command(
        command = "/start",
        aliases = {"старт", "начать", "start", "Сменить район"}
)
public class StartCommand extends TelegramCommand {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UsersManager usersManager;
    private final ComplaintManager complaintManager;

    public StartCommand(@NonNull UsersManager usersManager, @NonNull ComplaintManager complaintManager) {
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
                case USER -> {
                    usersManager.removeDistrict(chatId);
                    new MessageBuilder(client)
                            .send(context.message,
                                    config.get("command_start_message", "command_start_message"),
                                    new KeyboardBuilder().getKeyboard(getKeyboardButtons(config)));
                }
                case DISTRICT_HEAD, GOVERNOR -> new MessageBuilder(client)
                        .send(context.message,
                                config.get("admin_hello_message", "admin_hello_message"),
                                new KeyboardBuilder().getKeyboard(getAdminKeyboardButtons()));
            }


        } catch (Exception e) {
            logger.error("Error on {} command", this.getClass().getSimpleName(),  e);
        }
    }

    private List<String> getKeyboardButtons(Config config) {
        val districts = config.config.getArrayOrEmpty("districts");
        val array = districts.toList().stream()
                .map(object -> Objects.toString(object, null))
                .toList();
        return array;
    }

    private List<String> getAdminKeyboardButtons() {
        return List.of("Просмотреть жалобы");
    }
}
