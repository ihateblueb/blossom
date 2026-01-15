package site.remlit.blossom.service

import com.hypixel.hytale.server.core.universe.Universe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import site.remlit.blossom.Coroutines
import site.remlit.blossom.Main
import site.remlit.blossom.exception.GracefulException
import site.remlit.blossom.util.sendMessage
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

object MsgService {
    data class Conversation(
        val from: UUID,
        val to: UUID,
        var updatedAt: Instant = Clock.System.now()
    )

    /**
     * Recent conversations, first being sending user, second being receiving.
     * */
    val conversations = mutableListOf<Conversation>()

    fun startCleaner() {
        Main.logger.atInfo().log("Started message cleaner")
        Coroutines.sharedScope.launch {
            delay(5.seconds)
            conversations.filter { it.updatedAt < (Clock.System.now().minus(5.minutes)) }
                .forEach { conversations.remove(it) }
        }
    }

    fun send(from: UUID, to: UUID, msg: String) {
        val sender = Universe.get().players.first { it.uuid == from }
        val target = Universe.get().players.first { it.uuid == to }

        val conversation = conversations.filter { it.from == from && it.to == to }
        if (conversation.isEmpty())
            conversations.add(Conversation(from, to))
        else conversations.forEach {
            it.updatedAt = Clock.System.now()
        }

        target.sendMessage("<gray>${sender.username} whispers: $msg")
        sender.sendMessage("<gray>You whisper to ${target.username}: $msg")
    }

    fun reply(from: UUID, msg: String) {
        val conversation = conversations.sortedBy { it.updatedAt }
            .firstOrNull { it.from == from }
            ?: throw GracefulException("No conversation found")

        send(from, conversation.to, msg)
    }
}