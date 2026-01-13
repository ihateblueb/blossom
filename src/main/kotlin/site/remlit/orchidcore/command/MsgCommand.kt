package site.remlit.orchidcore.command

import com.hypixel.hytale.server.core.command.system.AbstractCommand
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.universe.PlayerRef
import site.remlit.orchidcore.exception.GracefulException
import site.remlit.orchidcore.service.MsgService
import site.remlit.orchidcore.util.sendMessage
import java.util.concurrent.CompletableFuture

class MsgCommand : AbstractCommand(
    "msg",
    "Messages a player",
) {
    val targetPlayerArg: RequiredArg<PlayerRef> = withRequiredArg<PlayerRef>(
        "targetPlayer",
        "Player to send message to",
        ArgTypes.PLAYER_REF
    )

    val messageArg: RequiredArg<String> = withRequiredArg<String>(
        "message",
        "Message to send to player",
        ArgTypes.STRING
    )

    init {
        this.requirePermission("orchidcore.command.msg")
        this.addAliases("whisper", "w")
    }

    override fun execute(ctx: CommandContext): CompletableFuture<Void> =
        CompletableFuture.runAsync {
            if (!ctx.isPlayer) {
                ctx.sendMessage("Only players can issue this command")
                return@runAsync
            }

            val target = targetPlayerArg.get(ctx)
            val sender = ctx.sender()
            val msg = messageArg.get(ctx)

            try {
                MsgService.send(sender.uuid, target.uuid, msg)
            } catch (e: GracefulException) {
                ctx.sendMessage(e.message ?: "Command failed")
            }
        }
}