package kiinse.dev.telegram.utils;

import kiinse.dev.telegram.complaint.Complaint;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageBuilder {

    private OkHttpTelegramClient client;

    public MessageBuilder(@NonNull OkHttpTelegramClient client) {
        this.client = client;
    }

    public void send(@NonNull Message from, @NonNull String message) throws TelegramApiException {
        send(from.getChatId(), message);
    }

    public void send(@NonNull Long chatId, @NonNull String message) throws TelegramApiException {
        client.execute(getSendMessage(chatId, message));
    }

    public void send(@NonNull Message from, @NonNull String message, @Nullable ReplyKeyboardMarkup keyboard) throws TelegramApiException {
        send(from.getChatId(), message, keyboard);
    }

    public void send(@NonNull Message from, @NonNull String message, @Nullable ReplyKeyboardMarkup keyboard, @NonNull Complaint complaint) throws TelegramApiException {
        send(from.getChatId(), message, keyboard, complaint);
    }

    public void send(@NonNull Long chatId, @NonNull String message, @Nullable ReplyKeyboardMarkup keyboard) throws TelegramApiException {
        client.execute(getSendMessage(chatId, message, keyboard));
    }

    public void send(@NonNull Long chatId, @NonNull String message, @Nullable ReplyKeyboardMarkup keyboard, @NonNull Complaint complaint) throws TelegramApiException {
        client.execute(getSendMessage(chatId, message, keyboard));
        val images = getImagesByComplaint(complaint.id);
        if (!images.isEmpty()) {
            if (images.size() > 1) {
                client.execute(getSendGroupPhoto(chatId, images));
            } else {
                client.execute(getSendPhoto(chatId, images));
            }
        }
    }

    private SendMessage getSendMessage(@NonNull Long chatId, @NonNull String message) {
        return new SendMessage(chatId.toString(), message);
    }

    private SendMessage getSendMessage(@NonNull Long chatId, @NonNull String message, @Nullable ReplyKeyboardMarkup keyboard) {
        val msg = new SendMessage(chatId.toString(), message);
        msg.setReplyMarkup(Objects.requireNonNullElseGet(keyboard, () -> new ReplyKeyboardRemove(true)));
        return msg;
    }

    private SendMediaGroup getSendGroupPhoto(@NonNull Long chatId, @NonNull List<File> images) {
        return new SendMediaGroup(chatId.toString(), getInputMedia(images));
    }

    private SendPhoto getSendPhoto(@NonNull Long chatId, @NonNull List<File> images) {
        val photo = images.getFirst();
        return new SendPhoto(chatId.toString(), new InputFile(photo));
    }


    private @NonNull List<InputMedia> getInputMedia(@NonNull List<File> images) {
        ArrayList<InputMedia> inputs = new ArrayList<>();
        for (val image : images) {
            inputs.add(new InputMediaPhoto(image, image.getName()));
        }
        return inputs;
    }

    private @NonNull List<File> getImagesByComplaint(String complaintId) {
        val dir = new File("photos").listFiles();
        List<File> files = new ArrayList<>();
        if (dir == null) { return files; }
        for (val file : dir) {
            if (file.getName().contains(complaintId)) {
                files.add(file);
            }
        }
        return files;
    }
}
