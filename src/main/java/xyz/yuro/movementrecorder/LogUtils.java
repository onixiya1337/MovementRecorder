package xyz.yuro.movementrecorder;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class LogUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public synchronized static void sendLog(ChatComponentText chat) {
        if(mc.thePlayer != null)
            mc.thePlayer.addChatMessage(chat);
    }
    public static void sendMessage(String message) {
        sendLog(new ChatComponentText(
                EnumChatFormatting.DARK_AQUA + "Movement Recorder " + EnumChatFormatting.DARK_GRAY + "» " + EnumChatFormatting.GRAY + message
        ));
    }
    public static void sendError(String message) {
        sendLog(new ChatComponentText(
                EnumChatFormatting.DARK_RED + "Movement Recorder " + EnumChatFormatting.DARK_GRAY + "» " + EnumChatFormatting.RED + message
        ));
    }
    public static void sendSuccess(String message) {
        sendLog(new ChatComponentText(
                EnumChatFormatting.DARK_GREEN + "Movement Recorder " + EnumChatFormatting.DARK_GRAY + "» " + EnumChatFormatting.GREEN + message
        ));
    }
}
