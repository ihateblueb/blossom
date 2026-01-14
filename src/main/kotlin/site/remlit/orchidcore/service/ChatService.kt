package site.remlit.orchidcore.service

import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.Universe
import site.remlit.orchidcore.util.blue

object ChatService {
    private fun chatFormatter(sender: PlayerRef, message: String): Message {
        return Message.join(
            blue { "${sender.username}:" },
            Message.raw(" $message")
        )
    }

    fun setup() {
        Universe.get().eventRegistry.registerGlobal(PlayerChatEvent::class.java) { event ->
            event.setFormatter(::chatFormatter)
        }
    }
}