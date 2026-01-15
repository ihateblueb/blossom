package site.remlit.blossom.service

import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.Universe
import site.remlit.blossom.Main
import site.remlit.blossom.formatter.Formatter.format
import site.remlit.blossom.formatter.Formatter.mergePlaceholders
import site.remlit.blossom.formatter.Formatter.playerPlaceholders

object ChatService {
    private fun chatFormatter(sender: PlayerRef, message: String): Message =
        format(
            mergePlaceholders(
                playerPlaceholders(sender),
                mapOf("msg" to message),
            ),
            Main.config.chat.format
        )

    fun setup() {
        Universe.get().eventRegistry.registerGlobal(PlayerChatEvent::class.java) { event ->
            if (Main.config.chat.enabled) return@registerGlobal

            fun cancelMsg(message: String) {
                event.isCancelled = true
                Main.logger.atInfo().log("Blocked message from ${event.sender.username}: $message")
                event.sender.sendMessage(format("<red>Your message has been blocked"))
            }

            Main.config.chat.bannedWords.forEach {
                if (event.content.contains(it)) cancelMsg(event.content)
            }

            Main.config.chat.bannedWordsRegex.forEach {
                if (event.content.contains(it.toRegex())) cancelMsg(event.content)
            }

            event.setFormatter(::chatFormatter)
        }
    }
}