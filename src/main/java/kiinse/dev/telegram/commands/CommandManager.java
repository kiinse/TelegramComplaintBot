package kiinse.dev.telegram.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import kiinse.dev.telegram.commands.annotations.Command;
import kiinse.dev.telegram.commands.interfaces.*;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;

public class CommandManager {

    private final HashMap<List<String>, TelegramCommand> commands = new HashMap<>();

    public boolean registerCommand(@NonNull TelegramCommand command) {
        val annotation = command.getClass().getAnnotation(Command.class);
        var cmds = new ArrayList<>(Arrays.stream(annotation.aliases()).toList());
        cmds.add(annotation.command());
        if (hasCommand(command)) return false;
        commands.put(cmds, command);
        return true;
    }

    public @Nullable TelegramCommand getCommand(@NonNull String cmd) {
        for (val command : commands.entrySet()) {
            for (val key : command.getKey()) {
                if ((key.toLowerCase().replace(" ", "").equals(cmd.toLowerCase().replace(" ", "")))) {
                    return command.getValue();
                }
            }
        }
        return null;
    }

    private boolean hasCommand(@NonNull TelegramCommand cmd) {
        for (val command : commands.values()) {
            if (command.hashCode() == cmd.hashCode()) {
                return true;
            }
        }
        return false;
    }
}
