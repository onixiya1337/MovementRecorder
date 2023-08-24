package xyz.yuro.movementrecorder;

import cc.polyfrost.oneconfig.gui.pages.Page;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.utils.InputHandler;

import cc.polyfrost.oneconfig.gui.elements.BasicButton;
import cc.polyfrost.oneconfig.utils.color.ColorPalette;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.File;

public class RecordingList extends Page {
    public RecordingList() {
        super("Recordings");
    }

    @Override
    public void draw(long vg, int x, int y, InputHandler inputHandler) {
        int iX = x + 16;
        int iY = y + 16;
        Minecraft mc = Minecraft.getMinecraft();
        File recordingDir = new File(mc.mcDataDir, "movementrecorder");
        if (!recordingDir.exists() || !recordingDir.isDirectory()) {
            NanoVGHelper.INSTANCE.drawText(vg, "No recordings found", iX + 338, iY + 348, Color.WHITE.getRGB(), 36f, Fonts.REGULAR);
            return;
        }
        File[] recordingFiles = recordingDir.listFiles();
        if (recordingFiles == null || recordingFiles.length == 0) {
            NanoVGHelper.INSTANCE.drawText(vg, "No recordings found", iX + 338, iY + 348, Color.WHITE.getRGB(), 36f, Fonts.REGULAR);
            return;
        }
        for (File file : recordingFiles) {
            if (file.isFile() && file.getName().endsWith(".movement")) {
                NanoVGHelper.INSTANCE.drawRoundedRect(vg, iX, iY, 1008, 50, new Color(50,50,50, 120).getRGB(), 12);
                NanoVGHelper.INSTANCE.drawText(vg, file.getName().replace(".movement", ""), iX + 12, iY + 24, Color.WHITE.getRGB(), 18f, Fonts.REGULAR);
                BasicButton deleteButton = new BasicButton(120, BasicButton.SIZE_36, "Delete", null, null, BasicButton.ALIGNMENT_CENTER, ColorPalette.PRIMARY_DESTRUCTIVE);
                deleteButton.setClickAction(() -> {
                    MovementRecorder.deleteRecording(file.getName().replace(".movement", ""));
                });
                deleteButton.draw(vg, iX + 882, iY + 6, inputHandler);
                BasicButton playButton = new BasicButton(120, BasicButton.SIZE_36, "Play", null, null, BasicButton.ALIGNMENT_CENTER, ColorPalette.PRIMARY);
                playButton.setClickAction(() -> {
                    Minecraft.getMinecraft().thePlayer.closeScreen();
                    MovementRecorder.playRecording(file.getName().replace(".movement", ""));
                });
                playButton.draw(vg, iX + 756, iY + 6, inputHandler);
                iY += 64;
            }
        }
    }
}
