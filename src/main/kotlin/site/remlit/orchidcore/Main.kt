package site.remlit.orchidcore

import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import site.remlit.orchidcore.command.*
import site.remlit.orchidcore.service.MsgService

class Main(init: JavaPluginInit) : JavaPlugin(init) {
    override fun setup() {
        MsgService.startCleaner()

        commandRegistry.registerCommand(MsgCommand())
        commandRegistry.registerCommand(ReplyCommand())

        commandRegistry.registerCommand(TpacceptCommand())
        commandRegistry.registerCommand(TpaCommand())
        commandRegistry.registerCommand(TpdenyCommand())
    }
}