package kiinse.dev.telegram.commands.command;

import kiinse.dev.telegram.commands.data.CommandContext;
import kiinse.dev.telegram.commands.interfaces.TelegramCommand;
import kiinse.dev.telegram.configuration.Config;
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

public class SelectDistrictCommand extends TelegramCommand {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UsersManager usersManager;
    private final List<String> districts;

    public SelectDistrictCommand(@NonNull UsersManager usersManager, @NonNull Config config) {
        this.usersManager = usersManager;
        this.districts = config.config.getArrayOrEmpty("districts").toList().stream()
                .map(object -> Objects.toString(object, null))
                .toList();
    }

    public void onCommand(@NonNull CommandContext context, @NonNull OkHttpTelegramClient client, @NonNull Config config) {
        try {
            if (config.isDebug) {
                logger.info("Executing {} to {}", this.getClass().getSimpleName(),  context.chatId);
            }
            val messageText = context.message.getText().toLowerCase().replace(" ", "");
            if (hasDistrict(messageText)) {
                usersManager.setUserDistrict(context.chatId, messageText);
                new MessageBuilder(client)
                        .send(context.message,
                              config.get("district_set", "district_set").replace("{}", messageText));
                new MessageBuilder(client)
                        .send(context.message,
                                config.get("complaint_readme", "complaint_readme"),
                                new KeyboardBuilder().getKeyboard(getKeyboardButtons()));
            } else {
                new MessageBuilder(client)
                        .send(context.message,
                              config.get("district_not_found", "district_not_found").replace("{}", messageText),
                              null);
            }
        } catch (Exception e) {
            logger.error("Error on {} command", this.getClass().getSimpleName(),  e);
        }
    }

    private boolean hasDistrict(String district) {
        for (val dst : this.districts) {
            if (dst.equalsIgnoreCase(district)) {
                return true;
            }
        }
        return false;
    }

    private List<String> getKeyboardButtons() {
        return Arrays.asList(
                "Отправить жалобу",
                "Просмотреть жалобу",
                "Удалить жалобу",
                "Сменить район"
        );
    }
}
