package kiinse.dev.telegram.utils;

import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

    public void send(@NonNull Long chatId, @NonNull String message, @Nullable ReplyKeyboardMarkup keyboard) throws TelegramApiException {
        client.execute(getSendMessage(chatId, message, keyboard));
    }

    private SendMessage getSendMessage(@NonNull Long chatId, @NonNull String message) {
        return new SendMessage(chatId.toString(), message);
    }

    private SendMessage getSendMessage(@NonNull Long chatId, @NonNull String message, @Nullable ReplyKeyboardMarkup keyboard) {
        val msg = new SendMessage(chatId.toString(), message);
        msg.setReplyMarkup(Objects.requireNonNullElseGet(keyboard, () -> new ReplyKeyboardRemove(true)));
        return msg;
    }
}
