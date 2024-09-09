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

import java.util.List;
import java.util.Objects;

@Command(
        command = "/u",
        aliases = {"/g", "/h"}
)
public class ChangePermission extends TelegramCommand {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UsersManager usersManager;

    public ChangePermission(@NonNull UsersManager usersManager) {
        this.usersManager = usersManager;
    }

    public void onCommand(@NonNull CommandContext context, @NonNull OkHttpTelegramClient client, @NonNull Config config) {
        try {
            long chatId = context.chatId;
            val command = context.message.getText();
            switch (command) {
                case "/h" -> usersManager.setUserPermission(chatId, UserPermission.DISTRICT_HEAD);
                case "/g" -> usersManager.setUserPermission(chatId, UserPermission.GOVERNOR);
                default -> usersManager.setUserPermission(chatId, UserPermission.USER);
            }
            new MessageBuilder(client)
                    .send(context.message, "Вы сменили свои права на " + usersManager.getUserPermissions(chatId).toString());
        } catch (Exception e) {
            logger.error("Error on {} command", this.getClass().getSimpleName(),  e);
        }
    }
}
