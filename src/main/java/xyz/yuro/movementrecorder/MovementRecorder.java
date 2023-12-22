package xyz.yuro.movementrecorder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MovementRecorder {
    private static List<Movement> movements = new ArrayList<>();
    private static boolean isMovementRecording = false;
    private static boolean isMovementPlaying = false;
    private static boolean isMovementReading = false;
    private static int currentDelay = 0;
    private static int playingIndex = 0;
    private static float yawDifference = 0;
    private static String recordingName = "";
    static Minecraft mc = Minecraft.getMinecraft();
    private static RotationUtils rotateBeforePlaying = new RotationUtils();
    private static RotationUtils rotateDuringPlaying = new RotationUtils();

    public static class Movement {
        private final boolean forward;
        private final boolean left;
        private final boolean backwards;
        private final boolean right;
        private final boolean sneak;
        private final boolean sprint;
        private final boolean fly;
        private final boolean jump;
        private final boolean attack;
        private final float yaw;
        private final float pitch;
        private int delay;

        public Movement(boolean forward, boolean left, boolean backwards, boolean right, boolean sneak, boolean sprint, boolean fly,
                        boolean jump, boolean attack, float yaw, float pitch, int delay) {
            this.forward = forward;
            this.left = left;
            this.backwards = backwards;
            this.right = right;
            this.sneak = sneak;
            this.sprint = sprint;
            this.fly = fly;
            this.jump = jump;
            this.attack = attack;
            this.yaw = yaw;
            this.pitch = pitch;
            this.delay = delay;
        }
        public String toCsv() {
            return forward + ";" + left + ";" + backwards + ";" + right + ";" +
                    sneak + ";" + sprint + ";" + fly + ";" + jump + ";" +
                    attack + ";" + yaw + ";" + pitch + ";" + delay;
        }
    }

    @SubscribeEvent
    public void onTickRecordMovement(ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;
        if (!isMovementRecording)
            return;

        Movement currentMovement = getCurrentMovement();

        if (!movements.isEmpty()) {
            Movement previousMovement = movements.get(movements.size() - 1);
            if (currentMovement.forward == previousMovement.forward &&
                    currentMovement.left == previousMovement.left &&
                    currentMovement.backwards == previousMovement.backwards &&
                    currentMovement.right == previousMovement.right &&
                    currentMovement.sneak == previousMovement.sneak &&
                    currentMovement.sprint == previousMovement.sprint &&
                    currentMovement.fly == previousMovement.fly &&
                    currentMovement.jump == previousMovement.jump &&
                    currentMovement.attack == previousMovement.attack &&
                    currentMovement.yaw == previousMovement.yaw &&
                    currentMovement.pitch == previousMovement.pitch) {
                previousMovement.delay++;
                return;
            }
        }
        movements.add(currentMovement);
    }

    @SubscribeEvent
    public void onTickPlayMovement(ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;
        if (!isMovementPlaying || isMovementReading)
            return;
        if (movements.isEmpty()) {
            LogUtils.sendError("The file is empty!");
            stopRecording();
            return;
        }
        if (rotateBeforePlaying.rotating) {
            KeyBindUtils.stopMovement();
            return;
        }

        Movement movement = movements.get(playingIndex);
        setPlayerMovement(movement);
        rotateDuringPlaying.easeTo(
                MovementRecorderConfig.useRelativeYaw ? AngleUtils.normalizeAngle(movement.yaw - yawDifference) : movement.yaw, movement.pitch, 49);

        if (currentDelay < movement.delay) {
            currentDelay++;
            return;
        }
        playingIndex++;
        currentDelay = 0;
        if (playingIndex >= movements.size()) {
            isMovementPlaying = false;
            resetTimers();
            LogUtils.sendMessage("Playing has been finished.");
        }
    }

    @SubscribeEvent
    public void onWorldLastRender(RenderWorldLastEvent event) {
        if (rotateDuringPlaying.rotating) {
            rotateDuringPlaying.update();
            return;
        }
        if (rotateBeforePlaying.rotating) {
            rotateBeforePlaying.update();
        }
    }

    public static void startRecording(String name) {
        if (isMovementRecording) {
            LogUtils.sendError("Recording has already started.");
            return;
        }
        if (isMovementPlaying) {
            LogUtils.sendError("The recording is playing now.");
            return;
        }
        if (isMovementReading) {
            LogUtils.sendError("The recording is being read now.");
            return;
        }
        movements.clear();
        playingIndex = 0;
        currentDelay = 0;
        recordingName = name;
        isMovementPlaying = false;
        isMovementRecording = true;
        LogUtils.sendSuccess("Recording " + recordingName + " has been started.");
        LogUtils.sendMessage("Type /movrec stop to stop recording.");
    }

    public static void stopRecording() {
        playingIndex = 0;
        currentDelay = 0;
        resetTimers();
        KeyBindUtils.stopMovement();
        if (isMovementRecording) {
            isMovementRecording = false;
            saveRecording();
            LogUtils.sendSuccess("Recording has been stopped.");
            return;
        }
        if (isMovementPlaying || isMovementReading) {
            isMovementPlaying = false;
            isMovementReading = false;
            LogUtils.sendSuccess("Playing has been stopped.");
            return;
        }
        LogUtils.sendError("No recording has been started.");
    }

    public static void playRecording(String name) {
        if (isMovementRecording) {
            LogUtils.sendError("You are recording now!");
            LogUtils.sendError("Type /movrec stop to stop recording.");
            return;
        }
        if (isMovementPlaying) {
            LogUtils.sendError("The recording is playing already.");
            return;
        }
        movements.clear();
        playingIndex = 0;
        resetTimers();
        isMovementReading = true;
        try {
            List<String> lines = java.nio.file.Files.readAllLines(new File(mc.mcDataDir + "\\movementrecorder\\" + name + ".movement").toPath());
            for (String line : lines) {
                if (!isMovementReading) return;
                String[] split = line.split(";");
                Movement movement = new Movement(
                        Boolean.parseBoolean(split[0]),
                        Boolean.parseBoolean(split[1]),
                        Boolean.parseBoolean(split[2]),
                        Boolean.parseBoolean(split[3]),
                        Boolean.parseBoolean(split[4]),
                        Boolean.parseBoolean(split[5]),
                        Boolean.parseBoolean(split[6]),
                        Boolean.parseBoolean(split[7]),
                        Boolean.parseBoolean(split[8]),
                        Float.parseFloat(split[9]),
                        Float.parseFloat(split[10]),
                        Integer.parseInt(split[11])
                );
                movements.add(movement);
            }
        } catch (Exception e) {
            LogUtils.sendError(EnumChatFormatting.RED + "An error occurred while playing the recording.");
            e.printStackTrace();
            isMovementReading = false;
            return;
        }
        isMovementReading = false;
        isMovementPlaying = true;
        Movement movement = movements.get(0);
        yawDifference = AngleUtils.normalizeAngle(movement.yaw - AngleUtils.get360RotationYaw());
        rotateBeforePlaying.easeTo((MovementRecorderConfig.useRelativeYaw ? mc.thePlayer.rotationYaw : movement.yaw), movement.pitch, 500);
    }

    private static void saveRecording() {
        File recordingDir = new File(mc.mcDataDir, "movementrecorder");
        if (!recordingDir.exists()) {
            boolean created = recordingDir.mkdirs();
            if (!created) {
                LogUtils.sendError("Failed to create recording directory.");
                return;
            }
        }

        File recordingFile = new File(recordingDir, recordingName + ".movement");
        try {
            if (!recordingFile.exists()) {
                boolean created = recordingFile.createNewFile();
                if (!created) {
                    LogUtils.sendError("Failed to create recording file.");
                    return;
                }
            }
            if (MovementRecorderConfig.removeStartDelay)
                movements.get(0).delay = 0;
            if (MovementRecorderConfig.removeEndDelay)
                movements.get(movements.size() - 1).delay = 0;
            try (PrintWriter pw = new PrintWriter(recordingFile)) {
                for (Movement movement : movements) {
                    pw.println(movement.toCsv());
                }
                LogUtils.sendSuccess("Recording " + recordingName + " has been saved.");
            } catch (Exception e) {
                LogUtils.sendError("An error occurred while saving the recording.");
                e.printStackTrace();
            }
        } catch (IOException e) {
            LogUtils.sendError("An error occurred while creating the recording file.");
            e.printStackTrace();
        }
        movements.clear();
    }

    public static void deleteRecording(String name) {
        if (isMovementRecording) {
            LogUtils.sendError("You are recording now!");
            LogUtils.sendError("Type /movrec stop to stop recording.");
            return;
        }
        if (isMovementPlaying) {
            LogUtils.sendError("The recording is playing now!");
            return;
        }
        if (isMovementReading) {
            LogUtils.sendError("The recording is being read now!");
            return;
        }
        File recordingDir = new File(mc.mcDataDir, "movementrecorder");
        File recordingFile = new File(recordingDir, name + ".movement");

        if (recordingFile.exists()) {
            try {
                boolean deleted = recordingFile.delete();
                if (deleted) {
                    LogUtils.sendSuccess("Recording " + name + " has been deleted.");
                } else {
                    LogUtils.sendError("Failed to delete recording " + name + ".");
                }
            } catch (SecurityException e) {
                LogUtils.sendError("Security exception occurred while deleting recording " + name + ".");
                e.printStackTrace();
            }
        } else {
            LogUtils.sendError("Recording " + name + " does not exist.");
        }
    }

    public static void listRecordings() {
        File recordingDir = new File(mc.mcDataDir, "movementrecorder");
        if (!recordingDir.exists() || !recordingDir.isDirectory()) {
            LogUtils.sendError("Recording directory does not exist.");
            return;
        }
        File[] recordingFiles = recordingDir.listFiles();
        if (recordingFiles == null) {
            LogUtils.sendError("An error occurred while listing recordings.");
            return;
        }
        if (recordingFiles.length == 0) {
            LogUtils.sendError("No recordings found.");
            return;
        }
        LogUtils.sendMessage("Recordings:");
        for (File file : recordingFiles) {
            if (file.isFile() && file.getName().endsWith(".movement")) {
                LogUtils.sendMessage("- " + file.getName().replace(".movement", ""));
            }
        }
    }


    private static void resetTimers() {
        rotateBeforePlaying.reset();
        rotateDuringPlaying.reset();
    }

    private void setPlayerMovement(Movement movement) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), movement.forward);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), movement.left);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), movement.backwards);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), movement.right);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), movement.sneak);
        mc.thePlayer.setSprinting(movement.sprint);
        if (mc.thePlayer.capabilities.allowFlying && mc.thePlayer.capabilities.isFlying != movement.fly)
            mc.thePlayer.capabilities.isFlying = movement.fly;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), movement.jump);
        if (movement.attack && currentDelay == 0)
            KeyBindUtils.leftClick();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), movement.attack);
    }

    private Movement getCurrentMovement() {
        return new Movement(
                mc.gameSettings.keyBindForward.isKeyDown(),
                mc.gameSettings.keyBindLeft.isKeyDown(),
                mc.gameSettings.keyBindBack.isKeyDown(),
                mc.gameSettings.keyBindRight.isKeyDown(),
                mc.gameSettings.keyBindSneak.isKeyDown(),
                mc.thePlayer.isSprinting(),
                mc.thePlayer.capabilities.isFlying,
                mc.gameSettings.keyBindJump.isKeyDown(),
                mc.gameSettings.keyBindAttack.isKeyDown(),
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch,
                0
        );
    }

    public static boolean isRecording() {
        return isMovementRecording;
    }

    public static boolean isPlaying() {
        return isMovementPlaying;
    }

    public static boolean isReading() {
        return isMovementReading;
    }
}
