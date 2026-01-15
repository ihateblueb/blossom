package site.remlit.blossom.service

import com.hypixel.hytale.builtin.teleport.components.TeleportHistory
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport
import com.hypixel.hytale.server.core.universe.Universe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import site.remlit.blossom.Coroutines
import site.remlit.blossom.Main
import site.remlit.blossom.exception.GracefulException
import site.remlit.blossom.util.sendMessage
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
        val pendingList = if (here) pendingTpaHeres else pendingTpas

        if (from == to)
            throw GracefulException("You cannot teleport to yourself")

        if (!pendingList.filter { (k, v) -> v == to }.isEmpty())
            throw GracefulException("This player already has someone trying to teleport to them")

        if (pendingList.contains(from))
            throw GracefulException("You already have an outgoing teleport request")

        pendingList[from] = to

        val sender = Universe.get().players.first { it.uuid == from }
        val target = Universe.get().players.first { it.uuid == to }

        val expireTime = Main.config.tpask.expireTime

        if (here) {
            target.sendMessage("<orange>${sender.username} has requested you teleport to them. You can accept with /tpaccept." +
                    " This will expire in $expireTime seconds.")
            sender.sendMessage("<orange>Sending request to teleport to ${target.username} to you." +
                    " It will expire in 30 seconds.")
        } else {
            target.sendMessage("<orange>${sender.username} has requested to teleport to you. You can accept with /tpaccept." +
                    " This will expire in $expireTime seconds.")
            sender.sendMessage("<orange>Sending request to teleport to ${target.username}." +
                    " It will expire in 30 seconds.")
        }

        Coroutines.sharedScope.launch {
            delay(expireTime.seconds)
            if (pendingList.contains(from)) {
                pendingList.remove(from)

                sender.sendMessage("<red>Teleportation request to ${target.username} expired.")
                target.sendMessage("<red>Teleportation request from ${sender.username} expired.")
            }
        }
    }

    fun acceptTpa(to: UUID) {
        val here = pendingTpaHeres.any { it.key == to }
        val pendingList = if (here) pendingTpaHeres else pendingTpas

        if (pendingList.filter { (k, v) -> v == to }.isEmpty())
            throw GracefulException("No pending teleportation requests")

        pendingList.filter { (k, v) -> v == to }
            .forEach { (k, v) ->
                val sender = Universe.get().players.first { it.uuid == k }
                val target = Universe.get().players.first { it.uuid == v }

                val waitTime = Main.config.tpask.teleportWait

                if (here) {
                    sender.sendMessage("<orange>${target.username} accepted your teleportation request.")
                    target.sendMessage("<orange>Teleporting to ${sender.username}" +
                            if (waitTime < 1 ) "." else " in $waitTime seconds.")
                } else {
                    sender.sendMessage("<orange>${target.username} accepted your teleportation request" +
                        if (waitTime < 1 ) "." else ", you'll be teleported in $waitTime seconds.")
                    target.sendMessage("<orange>Teleporting ${sender.username} to you.")
                }

                Coroutines.sharedScope.launch {
                    delay(waitTime.seconds)

                    val teleportPlayer = if (here) sender else target
                    val teleportPlayerRef = teleportPlayer.reference
                        ?: throw GracefulException("Failed to find player reference")

                    val teleportToPlayer = if (here) target else sender

                    val world = Universe.get().getWorld(
                        (if (here) sender.worldUuid else target.worldUuid)
                            ?: throw GracefulException("Failed to find world")
                    ) ?: throw GracefulException("Failed to find world")

                    // Extract to generic TeleportService.teleport method
                    teleportPlayerRef.store.addComponent(
                        teleportPlayerRef,
                        Teleport.getComponentType(),
                        Teleport(
                            world,
                            teleportToPlayer.transform.position,
                            teleportToPlayer.transform.rotation
                        )
                    )

                    teleportPlayerRef.store.ensureAndGetComponent(teleportPlayerRef, TeleportHistory.getComponentType())
                        .append(
                            world,
                            teleportToPlayer.transform.position,
                            teleportToPlayer.transform.rotation,
                            "Teleport ask to ${teleportToPlayer.username}"
                        )
                }

                pendingList.remove(k, v)
            }
    }

    fun denyTpa(to: UUID) {
        val here = pendingTpaHeres.any { it.key == to }
        val pendingList = if (here) pendingTpaHeres else pendingTpas

        if (pendingList.filter { (k, v) -> v == to }.isEmpty())
            throw GracefulException("No pending teleportation requests")

        pendingList.filter { (k, v) -> v == to }
            .forEach { (k, v) ->
                val sender = Universe.get().players.first { it.uuid == k }
                val target = Universe.get().players.first { it.uuid == v }

                sender.sendMessage("<red>${target.username} denied your teleportation request.")
                target.sendMessage("<orange>Denied teleportation request from ${sender.username}.")

                pendingList.remove(k, v)
            }
    }
}