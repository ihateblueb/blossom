package site.remlit.orchidcore.command

import com.hypixel.hytale.server.core.command.system.AbstractCommand
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import site.remlit.orchidcore.service.MsgService
import site.remlit.orchidcore.util.runCommand
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
        runCommand(ctx) {
            if (!ctx.isPlayer) {
                ctx.sendMessage("Only players can issue this command")
                return@runCommand
            }

            MsgService.reply(ctx.sender().uuid, messageArg.get(ctx))
        }
}