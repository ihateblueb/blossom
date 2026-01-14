package site.remlit.orchidcore.command

import com.hypixel.hytale.server.core.command.system.AbstractCommand
import com.hypixel.hytale.server.core.command.system.CommandContext
import site.remlit.orchidcore.exception.GracefulException
import site.remlit.orchidcore.service.TpaService
import site.remlit.orchidcore.util.red
import site.remlit.orchidcore.util.runCommand
import site.remlit.orchidcore.util.sendMessage
import java.util.concurrent.CompletableFuture

class TpdenyCommand : AbstractCommand(
    "tpdeny",
    "Denies a pending teleportation request",
) {
    init {
        this.requirePermission("orchidcore.command.tpdeny")
        this.addAliases("tpreject", "tpd", "tpr")
    }

    override fun execute(ctx: CommandContext): CompletableFuture<Void> =
        runCommand(ctx) {
            if (!ctx.isPlayer) {
                ctx.sendMessage("Only players can issue this command")
                return@runCommand
            }

            TpaService.denyTpa(ctx.sender().uuid)
        }
}