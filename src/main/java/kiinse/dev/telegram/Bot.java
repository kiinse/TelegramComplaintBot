package kiinse.dev.telegram;

import kiinse.dev.telegram.commands.CommandManager;
import kiinse.dev.telegram.commands.command.*;
import kiinse.dev.telegram.commands.data.CommandContext;
import kiinse.dev.telegram.configuration.Config;
import kiinse.dev.telegram.enums.UserPermission;
import kiinse.dev.telegram.manager.ComplaintManager;
import kiinse.dev.telegram.manager.UsersManager;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Bot implements LongPollingSingleThreadUpdateConsumer {

    private final Logger logger = LoggerFactory.getLogger("Bot");
    private final OkHttpTelegramClient telegramClient;
    private final CommandManager commandManager;
    private final Config config;
    private final UsersManager usersManager;
    private final SelectDistrictCommand selectDistrictCommand;
    private final SaveComplaintCommand saveComplaintCommand;


    public Bot(@NonNull OkHttpTelegramClient telegramClient,
               @NonNull CommandManager commandManager,
               @NonNull Config config,
               @NonNull UsersManager usersManager,
               @NonNull ComplaintManager complaintManager) {
        this.telegramClient = telegramClient;
        this.commandManager = commandManager;
        this.config = config;
        this.usersManager = usersManager;
        this.selectDistrictCommand = new SelectDistrictCommand(usersManager, config);
        this.saveComplaintCommand = new SaveComplaintCommand(usersManager, complaintManager);
    }


    public void consume(@Nullable Update update) {
        new Thread(() -> {
            try {
                if (update == null) { return; }
                val message = update.getMessage();
                val messageText = message.getText();
                val command = commandManager.getCommand(messageText);
                val chatId = message.getChatId();
                usersManager.createUser(chatId);
                if (config.isDebug) {
                    logger.info("Received new update! ChatId: {}", chatId);
                }
                val context = CommandContext.builder()
                        .chatId(chatId)
                        .message(message)
                        .user(message.getFrom())
                        .build();

                if (command instanceof ChangePermission) {
                    command.onCommand(context, telegramClient, config);
                } else {
                    if (usersManager.getUserPermissions(chatId) == UserPermission.USER) {
                        if (usersManager.isDistrictSelected(chatId)) {
                            if (command != null) {
                                command.onCommand(context, telegramClient, config);
                            } else {
                                saveComplaintCommand.onCommand(context, telegramClient, config);
                            }
                        } else {
                            if (command instanceof StartCommand) {
                                command.onCommand(context, telegramClient, config);
                            } else {
                                selectDistrictCommand.onCommand(context, telegramClient, config);
                            }
                        }
                    } else {
                        if (command instanceof StartCommand || command instanceof ViewComplaintAdminCommand) {
                            command.onCommand(context, telegramClient, config);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error while processing update {}", update, e);
            }
        }).start();
    }

}