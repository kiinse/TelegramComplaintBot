package kiinse.dev.telegram;

import kiinse.dev.telegram.commands.CommandManager;
import kiinse.dev.telegram.commands.command.*;
import kiinse.dev.telegram.commands.interfaces.TelegramCommand;
import kiinse.dev.telegram.configuration.Config;
import kiinse.dev.telegram.manager.ComplaintManager;
import kiinse.dev.telegram.manager.UsersManager;
import kiinse.dev.telegram.mongodb.MongoDB;
import kiinse.dev.telegram.mongodb.builder.DBSettings;
import kiinse.dev.telegram.mongodb.queries.UserQuery;
import kiinse.dev.telegram.mongodb.queries.ComplaintQuery;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import java.util.Arrays;
import java.util.List;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger("Main");
    private static final Config config = new Config();
    private static final MongoDB mongoDb = new MongoDB(getDBSettings());
    private static final ComplaintQuery complaintQuery = new ComplaintQuery(mongoDb);
    private static final UserQuery USER_QUERY = new UserQuery(mongoDb);
    private static final UsersManager usersManager = new UsersManager(USER_QUERY);
    private static final ComplaintManager complaintManager = new ComplaintManager(complaintQuery);
    private static final CommandManager commandManager = new CommandManager();
    private static final List<TelegramCommand> commands = Arrays.asList(
            new StartCommand(usersManager, complaintManager),
            new ChatIdCommand(),
            new SendComplaintCommand(usersManager, complaintManager),
            new ViewComplaintCommand(usersManager, complaintManager),
            new ViewComplaintAdminCommand(usersManager, complaintManager),
            new RemoveComplaintCommand(usersManager, complaintManager),
            new ChangePermission(usersManager)
    );

    public static void main(String[] args) {
        try {
            logger.info("Starting Telegram bot...");
            logger.info("Debug: {}", config.isDebug);
            val telegramToken = config.get("telegram_token", "telegram_token");
            if (telegramToken.equals("telegram bot token") || telegramToken.equals("telegram_token")) {
                throw new Exception("Bot token is empty! Please insert token in configs/config.toml!");
            }
            for (val command : commands) {
                if (commandManager.registerCommand(command)) {
                    logger.info("Registered command: {}", command.getClass().getSimpleName());
                }
            }
            val telegramClient = new OkHttpTelegramClient(telegramToken);
            new TelegramBotsLongPollingApplication().registerBot(telegramToken,
                    new Bot(telegramClient,
                            commandManager,
                            config,
                            usersManager,
                            complaintManager)
            );
            logger.info("Telegram bot started!");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static DBSettings getDBSettings() {
        val cfg = config.config.getTableOrEmpty("mongodb");
        return DBSettings.builder()
                .dbName(cfg.getString("dbName", () -> "telegram-complaint-bot"))
                .port(cfg.getString("port", () -> "27017"))
                .login(cfg.getString("login", () -> "admin"))
                .password(cfg.getString("password", () -> "admin"))
                .authDb(cfg.getString("authDb", () -> "admin"))
                .host(cfg.getString("host", () -> "localhost"))
                .build();
    }
}