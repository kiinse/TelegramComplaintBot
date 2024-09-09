package kiinse.dev.telegram.commands.interfaces;

import kiinse.dev.telegram.commands.data.CommandContext;
import kiinse.dev.telegram.configuration.Config;
import lombok.NonNull;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

public abstract class TelegramCommand {

    public abstract void onCommand(@NonNull CommandContext context, @NonNull OkHttpTelegramClient client, @NonNull Config config);

}
