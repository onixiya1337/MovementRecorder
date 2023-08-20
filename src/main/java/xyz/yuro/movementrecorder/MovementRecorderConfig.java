package xyz.yuro.movementrecorder;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

public class MovementRecorderConfig extends Config {

    public MovementRecorderConfig() {
        super(new Mod("Movement Recorder", ModType.SKYBLOCK, "/filepath/to/icon.png"), "movementrecorder.json");
        initialize();
    }
}