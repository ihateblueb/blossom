package site.remlit.orchidcore.util

import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.Universe

fun Universe.sendMessage(message: String) {
    this.sendMessage(Message.raw(message))
}

fun CommandContext.sendMessage(message: String) {
    this.sendMessage(Message.raw(message))
}

fun PlayerRef.sendMessage(message: String) {
    this.sendMessage(Message.raw(message))
}

fun gray(string: () -> String): Message {
    return Message.raw(string()).color("808080")
}

fun darkGray(string: () -> String): Message {
    return Message.raw(string()).color("474747")
}

fun blue(string: () -> String): Message {
    return Message.raw(string()).color("4554ff")
}

fun gold(string: () -> String): Message {
    return Message.raw(string()).color("ffb026")
}

fun red(string: () -> String): Message {
    return Message.raw(string()).color("ff3b3b")
}

fun msg(vararg msg: Message): Message {
    return Message.join(*msg)
}