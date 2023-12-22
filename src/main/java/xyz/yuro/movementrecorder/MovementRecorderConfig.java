package xyz.yuro.movementrecorder;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Button;
import cc.polyfrost.oneconfig.config.annotations.Page;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.annotations.Text;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.PageLocation;
import cc.polyfrost.oneconfig.utils.Multithreading;
import net.minecraft.client.Minecraft;

import java.util.concurrent.TimeUnit;

public class MovementRecorderConfig extends Config {
    public MovementRecorderConfig() {
        super(new Mod("Movement Recorder", ModType.UTIL_QOL), "movementrecorder.json");
        initialize();
        this.hideIf("_startRecording", () -> MovementRecorder.isRecording());
        this.hideIf("_stopRecording", () -> !MovementRecorder.isRecording());
    }

    @Page(name = "Recordings", location = PageLocation.TOP)
    public RecordingList recordingList = new RecordingList();

    @Text(
            name = "Recording name",
            placeholder = "Type your recording name here"
    )
    public static String recordingNameGUI = "";

    @Button(name = "Start recording", text = "Start"
    )
    Runnable _startRecording = () -> {
        if (recordingNameGUI == null || recordingNameGUI.isEmpty()) {
            LogUtils.sendError("Recording name cannot be empty!");
            return;
        }
        Multithreading.schedule(() -> {
            MovementRecorder.startRecording(recordingNameGUI);
        }, 250L, TimeUnit.MILLISECONDS);
        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    };

    @Button(name = "Stop recording", text = "Stop"
    )
    Runnable _stopRecording = () -> {
        MovementRecorder.stopRecording();
    };

    @Switch(
            name = "Remove delay at the beginning of the recording",
            description = "Removes the delay at the beginning of the recording, so it starts playing immediately."
    )
    public static boolean removeStartDelay = true;

    @Switch(
            name = "Remove delay at the end of the recording",
            description = "Removes the delay at the end of the recording, so you don't have to wait for the recording to end."
    )
    public static boolean removeEndDelay = true;

    @Switch(
            name = "Use relative yaw while playing",
            description = "Doesn't use fixed yaw from the recording while playing it, but instead uses the yaw relative to the player's yaw."
    )
    public static boolean useRelativeYaw = false;
}