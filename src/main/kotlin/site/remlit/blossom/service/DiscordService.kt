package site.remlit.blossom.service

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent
import com.hypixel.hytale.server.core.universe.Universe
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import site.remlit.blossom.Main
import site.remlit.blossom.exception.GracefulException
import site.remlit.blossom.formatter.Formatter
import site.remlit.blossom.formatter.Formatter.mergePlaceholders
import site.remlit.blossom.formatter.Formatter.playerPlaceholders
import site.remlit.blossom.util.sendMessage
import java.util.EnumSet

object DiscordService {
    class MessageReceiveListener : ListenerAdapter() {
        override fun onMessageReceived(event: MessageReceivedEvent) {
            if (event.channel.id != Main.config.discord.channelId)
                return

            if (event.author.isBot)
                return

            Main.logger.atInfo().log("(Discord) ${event.author.effectiveName}: ${event.message.contentRaw}")

            Universe.get().sendMessage(
                Formatter.format(
                    mapOf(
                        "username" to event.author.effectiveName,
                        "msg" to event.message.contentRaw
                    ),
                    Main.config.discord.discordToHytaleFormat
                )
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
                    Formatter.formatToString(
                        playerPlaceholders(event.playerRef),
                        Main.config.discord.joinFormat
                    )
                ).queue()
            }

            Universe.get().eventRegistry.registerGlobal(PlayerDisconnectEvent::class.java) { event ->
                channel.sendMessage(
                    Formatter.formatToString(
                        playerPlaceholders(event.playerRef),
                        Main.config.discord.leaveFormat
                    )
                ).queue()
            }

            Universe.get().eventRegistry.registerGlobal(PlayerChatEvent::class.java) { event ->
                channel.sendMessage(
                    Formatter.formatToString(
                        mergePlaceholders(
                            playerPlaceholders(event.sender),
                            mapOf("msg" to event.content)
                        ),
                        Main.config.discord.hytaleToDiscordFormat
                    )
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
        channel.sendMessage(Main.config.discord.serverStopFormat).complete()

        jda.shutdown()
    }
}