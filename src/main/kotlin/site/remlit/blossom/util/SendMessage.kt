package site.remlit.blossom.util

import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.Universe
import site.remlit.blossom.formatter.Formatter

fun Universe.sendMessage(message: String) {
    this.sendMessage(Formatter.format(message))
}

fun CommandContext.sendMessage(message: String) {
    this.sendMessage(Formatter.format(message))
}

fun PlayerRef.sendMessage(message: String) {
    this.sendMessage(Formatter.format(message))
}