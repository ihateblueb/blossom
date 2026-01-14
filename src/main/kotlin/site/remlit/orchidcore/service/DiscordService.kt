package site.remlit.orchidcore.service

import com.hypixel.hytale.component.event.EntityEventType
import com.hypixel.hytale.event.IEvent
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.event.events.entity.EntityEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerEvent
import com.hypixel.hytale.server.core.universe.Universe
import com.hypixel.hytale.server.flock.FlockDeathSystems
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import site.remlit.orchidcore.Main
import site.remlit.orchidcore.exception.GracefulException
import site.remlit.orchidcore.util.sendMessage
import java.util.EnumSet

object DiscordService {
    class MessageReceiveListener : ListenerAdapter() {
        override fun onMessageReceived(event: MessageReceivedEvent) {
            if (event.channel.id != Main.config.discord.channelId)
                return

            if (event.author.isBot)
                return

            Main.logger.atInfo().log("(Discord) ${event.author.name}: ${event.message.contentRaw}")

            Universe.get().sendMessage(
                Main.config.discord.discordToHytaleFormat
                    .replace("%username%", event.author.name)
                    .replace("%msg%", event.message.contentRaw)
            )
        }
    }

    var jdaStarted = false
    private lateinit var jda: JDA
    private lateinit var channel: TextChannel

    fun setup() {
        try {
            val intents = EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
            )

            jda = JDABuilder.createDefault(Main.config.discord.token, intents)
                .addEventListeners(MessageReceiveListener())
                .build()

            jda.awaitReady()
            jdaStarted = true

            Main.logger.atInfo().log("Attempting to find Discord channel ${Main.config.discord.channelId}")
            channel = jda.getTextChannelById(Main.config.discord.channelId)
                ?: throw GracefulException("Could not find Discord channel")

            channel.sendMessage(Main.config.discord.serverStartFormat).queue()

            Universe.get().eventRegistry.registerGlobal(PlayerConnectEvent::class.java) { event ->
                channel.sendMessage(
                    Main.config.discord.joinFormat
                        .replace("%username%", event.playerRef.username)
                ).queue()
            }

            Universe.get().eventRegistry.registerGlobal(PlayerDisconnectEvent::class.java) { event ->
                channel.sendMessage(
                    Main.config.discord.leaveFormat
                        .replace("%username%", event.playerRef.username)
                ).queue()
            }

            Universe.get().eventRegistry.registerGlobal(PlayerChatEvent::class.java) { event ->
                channel.sendMessage(
                    Main.config.discord.hytaleToDiscordFormat
                        .replace("%username%", event.sender.username)
                        .replace("%msg%", event.content)
                ).queue()
            }

            Main.logger.atInfo().log("Started Discord bridge")
        } catch (e: Throwable) {
            e.printStackTrace()
            Main.logger.atInfo().log("Failed to start Discord bridge: ${e.message}")
        }
    }

    fun shutdown() {
        if (!jdaStarted) return

        Main.logger.atInfo().log("Shutting down Discord bridge")
        channel.sendMessage(Main.config.discord.serverStopFormat).queue()

        jda.shutdown()
    }
}