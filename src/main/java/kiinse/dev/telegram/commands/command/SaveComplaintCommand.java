package kiinse.dev.telegram.commands.command;

import kiinse.dev.telegram.Bot;
import kiinse.dev.telegram.commands.data.CommandContext;
import kiinse.dev.telegram.commands.interfaces.TelegramCommand;
import kiinse.dev.telegram.configuration.Config;
import kiinse.dev.telegram.manager.ComplaintManager;
import kiinse.dev.telegram.manager.UsersManager;
import kiinse.dev.telegram.utils.KeyboardBuilder;
import kiinse.dev.telegram.utils.MessageBuilder;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class SaveComplaintCommand extends TelegramCommand {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UsersManager usersManager;
    private final ComplaintManager complaintManager;

    public SaveComplaintCommand(@NonNull UsersManager usersManager, @NonNull ComplaintManager complaintManager) {
        this.usersManager = usersManager;
        this.complaintManager = complaintManager;
    }

    public void onCommand(@NonNull CommandContext context, @NonNull OkHttpTelegramClient client, @NonNull Config config) {
        try {
            if (config.isDebug) {
                logger.info("Executing {} to {}", this.getClass().getSimpleName(),  context.chatId);
            }
            long chatId = context.chatId;
            val message = context.message;

            String text = getMessageText(message);

            val complaint = complaintManager.addComplaint(chatId, context.user.getUserName(), usersManager.getDistrict(chatId), text);

            if (message.hasPhoto()) {
                downloadPhotos(complaint.id, context.message, client);
            }
            new MessageBuilder(client)
                    .send(context.message,
                            config.get("complaint_view", "complaint_view"));
            new MessageBuilder(client)
                    .send(context.message,
                            "---Жалоба---\n" + complaint.text,
                            new KeyboardBuilder().getKeyboard(getKeyboardButtons()),
                            complaint);

        } catch (Exception e) {
            logger.error("Error on {} command", this.getClass().getSimpleName(),  e);
        }
    }

    private String getMessageText(Message message) {
        String text;
        if (message.hasText()) {
            text = message.getText();
        } else {
            text = message.getCaption();
        }
        if (text == null) {
            text = "";
        }
        return text;
    }

    public void downloadPhotos(String complaintId, Message message, OkHttpTelegramClient client)
    {
        PhotoSize photo = message.getPhoto().stream()
                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null);
        if (photo != null) {
            GetFile getFile = new GetFile(photo.getFileId());
            try
            {
                File file = client.execute(getFile);
                val downloadedFile = client.downloadFile(file);
                FileUtils.moveFile(downloadedFile, new java.io.File("photos/photo-" + complaintId + "-" + UUID.randomUUID() + ".png"));
            } catch (Exception e)
            {
                logger.error("Error on {} downloading photo", this.getClass().getSimpleName(),  e);
            }
        }
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
