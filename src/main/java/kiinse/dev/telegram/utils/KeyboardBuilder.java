package kiinse.dev.telegram.utils;

import lombok.val;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardBuilder {

    public ReplyKeyboardMarkup getKeyboard(List<String> buttons) {
        val keyboard = new ReplyKeyboardMarkup(getKeyboardRowList(buttons));
        keyboard.setSelective(true);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);
        return keyboard;
    }

    private List<KeyboardRow> getKeyboardRowList(List<String> buttons) {
        val list = new ArrayList<KeyboardRow>();
        for (String button : buttons) {
            list.add(new KeyboardRow(button));
        }
        return list;
    }
}
