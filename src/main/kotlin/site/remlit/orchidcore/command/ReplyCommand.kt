package site.remlit.orchidcore.command

import com.hypixel.hytale.server.core.command.system.AbstractCommand
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import site.remlit.orchidcore.exception.GracefulException
import site.remlit.orchidcore.service.MsgService
import site.remlit.orchidcore.util.sendMessage
import java.util.concurrent.CompletableFuture

class ReplyCommand : AbstractCommand(
    "reply",
    "Reply to the last player you messaged",
) {
    val messageArg: RequiredArg<String> = withRequiredArg<String>(
        "message",
        "Message to send to player",
        ArgTypes.STRING
    )

    init {
        this.requirePermission("orchidcore.command.reply")
        this.addAliases("r")
    }

    override fun execute(ctx: CommandContext): CompletableFuture<Void> =
        CompletableFuture.runAsync {
            if (!ctx.isPlayer) {
                ctx.sendMessage("Only players can issue this command")
                return@runAsync
            }

            val sender = ctx.sender()
            val msg = messageArg.get(ctx)

            try {
                MsgService.reply(sender.uuid, msg)
            } catch (e: GracefulException) {
                ctx.sendMessage(e.message ?: "Command failed")
            }
        }
}