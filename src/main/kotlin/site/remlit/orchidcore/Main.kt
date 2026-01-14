package site.remlit.orchidcore

import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import site.remlit.orchidcore.command.*
import site.remlit.orchidcore.model.Configuration
import site.remlit.orchidcore.service.ChatService
import site.remlit.orchidcore.service.DiscordService
import site.remlit.orchidcore.service.MsgService
import site.remlit.orchidcore.util.jsonConfig
import kotlin.io.path.*
import kotlin.system.measureTimeMillis


class Main(init: JavaPluginInit) : JavaPlugin(init) {
    override fun setup() {
        Companion.logger = this.logger

        val cfgTime = measureTimeMillis {
            val path = Path("./mods/OrchidCore/config.json")

            if (!path.parent.exists())
                path.parent.createDirectories()

            if (!path.exists()) {
                path.createFile()
            }

            if (path.readText().isEmpty())
                path.writeText(jsonConfig.encodeToString(Configuration()))

            config = try {
                jsonConfig.decodeFromString<Configuration>(path.readText())
            } catch (e: Throwable) {
                e.printStackTrace()
                logger.atWarning().log("Failed decoding configuration, using default. Some features may not work.")
                Configuration()
            }
        }

        logger.atInfo().log("Loaded configuration in $cfgTime ms")

        val cmdTime = measureTimeMillis {
            commandRegistry.registerCommand(MsgCommand())
            commandRegistry.registerCommand(ReplyCommand())

            commandRegistry.registerCommand(TpacceptCommand())
            commandRegistry.registerCommand(TpaCommand())
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
        lateinit var logger: HytaleLogger
        lateinit var config: Configuration
    }
}