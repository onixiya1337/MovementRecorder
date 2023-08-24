package xyz.yuro.movementrecorder;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Button;
import cc.polyfrost.oneconfig.config.annotations.Page;
import cc.polyfrost.oneconfig.config.annotations.Text;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.PageLocation;
import net.minecraft.client.Minecraft;

public class MovementRecorderConfig extends Config {
    public MovementRecorderConfig() {
        super(new Mod("Movement Recorder", ModType.UTIL_QOL), "movementrecorder.json");
        initialize();
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
    Runnable startRecording = () -> {
        if (recordingNameGUI == null) return;
        Minecraft.getMinecraft().thePlayer.closeScreen();
        MovementRecorder.startRecording(recordingNameGUI);
    };

    @Button(name = "Stop recording", text = "Stop"
    )
    Runnable stopRecording = () -> {
        MovementRecorder.stopRecording();
    };
}