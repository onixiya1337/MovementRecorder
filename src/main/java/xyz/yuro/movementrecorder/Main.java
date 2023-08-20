package xyz.yuro.movementrecorder;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "movementrecorder", name = "Movement Recorder", version = "%%VERSION%%")
public class Main {

    public static MovementRecorderConfig config;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new MovementRecorderConfig();
        MinecraftForge.EVENT_BUS.register(new MovementRecorder());
        ClientCommandHandler.instance.registerCommand(new MovementRecorderCommand());
    }

}
