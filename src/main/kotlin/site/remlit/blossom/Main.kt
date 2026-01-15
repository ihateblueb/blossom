package site.remlit.blossom

import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import site.remlit.blossom.command.*
import site.remlit.blossom.model.Configuration
import site.remlit.blossom.service.ChatService
import site.remlit.blossom.service.ConfigService
import site.remlit.blossom.service.DiscordService
import site.remlit.blossom.service.MsgService
import site.remlit.blossom.util.jsonConfig
import kotlin.io.path.*
import kotlin.system.measureTimeMillis


class Main(init: JavaPluginInit) : JavaPlugin(init) {
    override fun setup() {
        Companion.logger = this.logger

        val cfgTime = measureTimeMillis {
            ConfigService.load()
        }

        logger.atInfo().log("Loaded configuration in $cfgTime ms")

        val cmdTime = measureTimeMillis {
            commandRegistry.registerCommand(MsgCommand())
            commandRegistry.registerCommand(ReplyCommand())

            commandRegistry.registerCommand(TpacceptCommand())
            commandRegistry.registerCommand(TpaCommand())
            commandRegistry.registerCommand(TpahereCommand())
            commandRegistry.registerCommand(TpdenyCommand())
        }

        logger.atInfo().log("Registered commands in $cmdTime ms")

        val miscTime = measureTimeMillis {
            if (config.discord.enabled)
                DiscordService.setup()

            ChatService.setup()

            MsgService.startCleaner()
        }

        logger.atInfo().log("Initialized miscellaneous services in $miscTime ms")
    }

    override fun shutdown() {
        DiscordService.shutdown()
    }

    companion object {
        val configPath = Path("./mods/blossom/config.json")
        lateinit var logger: HytaleLogger
        lateinit var config: Configuration
    }
}