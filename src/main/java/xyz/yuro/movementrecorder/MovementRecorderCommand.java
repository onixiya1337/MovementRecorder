package xyz.yuro.movementrecorder;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovementRecorderCommand implements ICommand {
    protected static ArrayList<String> aliases = new ArrayList<>(Arrays.asList("movrec", "movementrecorder"));
    protected static ArrayList<String> tabCompletion = new ArrayList<>(Arrays.asList("start", "stop", "play", "delete", "list"));

    @Override
    public String getCommandName() {
        return "movrec";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/movrec";
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {
                MovementRecorder.startRecording(args[1]);
                return;
            } else if (args[0].equalsIgnoreCase("play")) {
                MovementRecorder.playRecording(args[1]);
                return;
            } else if (args[0].equalsIgnoreCase("delete")) {
                MovementRecorder.deleteRecording(args[1]);
                return;
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("stop")) {
                MovementRecorder.stopRecording();
                return;
            }
            if (args[0].equalsIgnoreCase("list")) {
                MovementRecorder.listRecordings();
                return;
            }
        }
        LogUtils.sendError("Invalid arguments. Usage: /movrec <start/play/delete/stop/list> <filename>");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return tabCompletion;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(@NotNull ICommand o) {
        return 0;
    }
}
