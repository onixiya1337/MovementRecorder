package de.tomjuri.examplemod

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import de.tomjuri.examplemod.mixin.MinecraftInvoker
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText

@Command(value = "examplecommand")
class ExampleCommand {

    @Main
    private fun main() {
        Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText("§c${ExampleMod.config.exampleText}"))
    }
}