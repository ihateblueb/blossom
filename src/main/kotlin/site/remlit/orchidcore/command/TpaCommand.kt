package site.remlit.orchidcore.command

import com.hypixel.hytale.server.core.command.system.AbstractCommand
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.universe.PlayerRef
import site.remlit.orchidcore.exception.GracefulException
import site.remlit.orchidcore.service.TpaService
import site.remlit.orchidcore.util.red
import site.remlit.orchidcore.util.runCommand
import site.remlit.orchidcore.util.sendMessage
import java.util.concurrent.CompletableFuture

class TpaCommand : AbstractCommand(
    "tpask",
    "Asks player if you can teleport to them"
) {
    val targetPlayerArg: RequiredArg<PlayerRef> = withRequiredArg<PlayerRef>(
        "targetPlayer",
        "Player to request to teleport to",
        ArgTypes.PLAYER_REF
    )

    init {
        this.requirePermission("orchidcore.command.tpask")
        this.addAliases("tpa")
    }

    override fun execute(ctx: CommandContext): CompletableFuture<Void> =
        runCommand(ctx) {
            if (!ctx.isPlayer) {
                ctx.sendMessage("Only players can issue this command")
                return@runCommand
            }

            val target = targetPlayerArg.get(ctx)

            TpaService.appendTpa(ctx.sender().uuid, target.uuid)
            ctx.sendMessage("Sending request to teleport to ${target.username}." +
                    " It will expire in 30 seconds.")
        }
}