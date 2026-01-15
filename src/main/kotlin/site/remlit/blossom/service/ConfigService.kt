package site.remlit.blossom.service

import site.remlit.blossom.Main
import site.remlit.blossom.Main.Companion.configPath
import site.remlit.blossom.formatter.FormatterConfiguration
import site.remlit.blossom.model.Configuration
import site.remlit.blossom.util.jsonConfig
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object ConfigService {
    fun load() {
        if (!configPath.parent.exists())
            configPath.parent.createDirectories()

        if (!configPath.exists()) {
            configPath.createFile()
        }

        if (configPath.readText().isEmpty())
            configPath.writeText(jsonConfig.encodeToString(Configuration()))

        var isDefault = false

        Main.config = try {
            jsonConfig.decodeFromString<Configuration>(configPath.readText())
        } catch (e: Throwable) {
            e.printStackTrace()
            isDefault = true
            Main.logger.atWarning().log("Failed decoding configuration, using default. Some features may not work.")
            Configuration()
        }

        if (!isDefault) upgrade()

        FormatterConfiguration.colors["red"] = Main.config.formatting.red
        FormatterConfiguration.colors["orange"] = Main.config.formatting.orange
        FormatterConfiguration.colors["yellow"] = Main.config.formatting.yellow
        FormatterConfiguration.colors["green"] = Main.config.formatting.green
        FormatterConfiguration.colors["blue"] = Main.config.formatting.blue
        FormatterConfiguration.colors["purple"] = Main.config.formatting.purple
        FormatterConfiguration.colors["pink"] = Main.config.formatting.pink
        FormatterConfiguration.colors["gray"] = Main.config.formatting.gray
        FormatterConfiguration.colors["darkgray"] = Main.config.formatting.darkgray
    }

    fun upgrade() {
        if (Main.config.version <= 0) migration1()
        if (Main.config.version <= 1) migration2()

        writeCurrent()
    }

    fun writeCurrent() = configPath.writeText(jsonConfig.encodeToString(Main.config))

    private fun migration1() {
        Main.config.chat.format.replace("%player%", "%username%")
        Main.config.discord.joinFormat.replace("%player%", "%username%")
        Main.config.discord.leaveFormat.replace("%player%", "%username%")
        Main.config.discord.hytaleToDiscordFormat.replace("%player%", "%username%")
        Main.config.discord.discordToHytaleFormat.replace("%player%", "%username%")

        Main.config.version = 1
    }

    private fun migration2() {
        Main.config.chat.format.replace("%username%", "%playerUsername%")
        Main.config.discord.joinFormat.replace("%username%", "%playerUsername%")
        Main.config.discord.leaveFormat.replace("%username%", "%playerUsername%")
        Main.config.discord.hytaleToDiscordFormat.replace("%username%", "%playerUsername%")
        Main.config.discord.discordToHytaleFormat.replace("%username%", "%playerUsername%")

        Main.config.version = 2
    }
}