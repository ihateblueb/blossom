package site.remlit.orchidcore.service

import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.Universe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import site.remlit.orchidcore.Coroutines
import site.remlit.orchidcore.Main
import site.remlit.orchidcore.exception.GracefulException
import site.remlit.orchidcore.util.gold
import site.remlit.orchidcore.util.red
import site.remlit.orchidcore.util.sendMessage
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

object TpaService {
    /**
     * Map of pending TPAs where first is requesting player, and second is destination
     * */
    val pendingTpas = mutableMapOf<UUID, UUID>()

    /**
     * Map of pending TPAs where first is requesting destination player, and second is target
     * */
    val pendingTpaHeres = mutableMapOf<UUID, UUID>()

    fun appendTpa(from: UUID, to: UUID, here: Boolean = false) {
        /*
        if (from == to)
            throw GracefulException("You cannot teleport to yourself")
            */

        if (!pendingTpas.filter { (k, v) -> v == to }.isEmpty())
            throw GracefulException("This player already has someone trying to teleport to them")

        if (pendingTpas.contains(from))
            throw GracefulException("You already have an outgoing teleport request")

        pendingTpas[from] = to

        val sender = Universe.get().players.first { it.uuid == from }
        val target = Universe.get().players.first { it.uuid == to }

        val expireTime = Main.config.tpask.expireTime

        target.sendMessage(gold { "${sender.username} has requested to teleport to you. You can accept with /tpaccept." +
                " This will expire in $expireTime seconds." })

        Coroutines.sharedScope.launch {
            delay(expireTime.seconds)
            if (pendingTpas.contains(from)) {
                pendingTpas.remove(from)

                sender.sendMessage(red { "Teleportation request to ${target.username} expired." })
                target.sendMessage(red { "Teleportation request from ${sender.username} expired." })
            }
        }
    }

    fun acceptTpa(to: UUID, here: Boolean = false) {
        if (pendingTpas.filter { (k, v) -> v == to }.isEmpty())
            throw GracefulException("No pending teleportation requests")

        pendingTpas.filter { (k, v) -> v == to }
            .forEach { (k, v) ->
                val sender = Universe.get().players.first { it.uuid == k }
                val target = Universe.get().players.first { it.uuid == v }

                val waitTime = Main.config.tpask.teleportWait

                sender.sendMessage(gold { "${sender.username} accepted your teleportation request, you'll be teleported" +
                        " in $waitTime seconds." })
                target.sendMessage(gold { "Teleporting ${sender.username} to you." })

                val targetWorld = Universe.get().getWorld(target.worldUuid ?: throw GracefulException("Something went wrong"))
                    ?: throw GracefulException("Something went wrong")

                Coroutines.sharedScope.launch {
                    delay(waitTime.seconds)
                    sender.updatePosition(targetWorld, target.transform, sender.headRotation)
                }

                pendingTpas.remove(k, v)
            }
    }

    fun denyTpa(to: UUID, here: Boolean = false) {
        if (pendingTpas.filter { (k, v) -> v == to }.isEmpty())
            throw GracefulException("No pending teleportation requests")

        pendingTpas.filter { (k, v) -> v == to }
            .forEach { (k, v) ->
                val sender = Universe.get().players.first { it.uuid == k }
                val target = Universe.get().players.first { it.uuid == v }

                sender.sendMessage(red { "${sender.username} denied your teleportation request." })
                target.sendMessage(gold { "Denied teleportation request from ${sender.username}." })

                pendingTpas.remove(k, v)
            }
    }
}