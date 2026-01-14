package site.remlit.orchidcore.model

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    val chat: ChatConfiguration = ChatConfiguration(),
    val tpask: TpaskConfiguration = TpaskConfiguration(),
    val discord: DiscordConfiguration = DiscordConfiguration(),
)

@Serializable
data class ChatConfiguration(
    val enabled: Boolean = true,
    val format: String = "%player%: %msg%",
)

@Serializable
data class TpaskConfiguration(
    val expireTime: Int = 30,
    val teleportWait: Int = 3,
)

@Serializable
data class DiscordConfiguration(
    val enabled: Boolean = false,
    val token: String = "CHANGEME",
    val channelId: String = "CHANGEME",

    val serverStartFormat: String = ":white_check_mark: **Server started**",
    val serverStopFormat: String = ":stop_sign: **Server stopped**",

    val joinFormat: String = ":arrow_right: **%player% joined the server**",
    val leaveFormat: String = ":arrow_left: **%player% left the server**",
    val deathFormat: String = ":headstone: **%player% died**",

    val hytaleToDiscordFormat: String = "**%username%:** %msg%",
    val discordToHytaleFormat: String = "(Discord) %username%: %msg%",
)
