package xyz.yuro.movementrecorder;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.PageLocation;
import cc.polyfrost.oneconfig.utils.Multithreading;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.TimeUnit;

public class MovementRecorderConfig extends Config {
    public MovementRecorderConfig() {
        super(new Mod("Movement Recorder", ModType.UTIL_QOL), "movementrecorder.json");
        initialize();
        this.hideIf("_startRecording", MovementRecorder::isRecording);
        this.hideIf("_stopRecording", () -> !MovementRecorder.isRecording());
        this.registerKeyBind(playStopRecording, () -> {
            if (MovementRecorder.isRecording()) {
                MovementRecorder.stopRecording();
            } else {
                if (recordingNameGUI == null || recordingNameGUI.isEmpty()) {
                    LogUtils.sendError("Recording name cannot be empty!");
                    return;
                }
                LogUtils.sendMessage("Setting yaw/pitch to 0...");
                Minecraft.getMinecraft().thePlayer.rotationYaw = 0;
                Minecraft.getMinecraft().thePlayer.rotationPitch = 0;
                LogUtils.sendMessage("Starting recording in 250ms...");
                Multithreading.schedule(() -> MovementRecorder.startRecording(recordingNameGUI), 250L, TimeUnit.MILLISECONDS);
            }
        });
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
    Runnable _stopRecording = MovementRecorder::stopRecording;

    @KeyBind(
            name = "Play/Stop recording",
            description = "Plays the recording if it's not playing, stops it if it's playing."
    )
    public OneKeyBind playStopRecording = new OneKeyBind(Keyboard.KEY_N);

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

    @Dropdown(
            name = "Rotation Difference",
            description = "The difference between the player's yaw and the recording's yaw.",
            options = {"Closest 90°", "Recording's yaw", "Player's yaw (aka relative)"}
    )
    public static int rotationType = 0;
}