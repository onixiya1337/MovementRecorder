package xyz.yuro.movementrecorder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindUtils
{
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void rightClick() {
        if (!ReflectionUtils.invoke(mc, "func_147121_ag")) {
            ReflectionUtils.invoke(mc, "rightClickMouse");
        }
    }

    public static void leftClick() {
        if (!ReflectionUtils.invoke(mc, "func_147116_af")) {
            ReflectionUtils.invoke(mc, "clickMouse");
        }
    }

    public static void stopMovement() {
        stopMovement(false);
    }

    public static void stopMovement(boolean ignoreAttack) {
        KeyBinding.setKeyBindState(KeyBindUtils.mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(KeyBindUtils.mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(KeyBindUtils.mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(KeyBindUtils.mc.gameSettings.keyBindLeft.getKeyCode(), false);
        if (!ignoreAttack) {
            KeyBinding.setKeyBindState(KeyBindUtils.mc.gameSettings.keyBindAttack.getKeyCode(), false);
        }
        KeyBinding.setKeyBindState(KeyBindUtils.mc.gameSettings.keyBindSneak.getKeyCode(), false);
        KeyBinding.setKeyBindState(KeyBindUtils.mc.gameSettings.keyBindJump.getKeyCode(), false);
        KeyBinding.setKeyBindState(KeyBindUtils.mc.gameSettings.keyBindSprint.getKeyCode(), false);
    }
}