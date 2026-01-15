package site.remlit.blossom.model

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    var version: Int = 0,
    var formatting: FormattingConfiguration = FormattingConfiguration(),
    var chat: ChatConfiguration = ChatConfiguration(),
    var tpask: TpaskConfiguration = TpaskConfiguration(),
    var discord: DiscordConfiguration = DiscordConfiguration(),
)

@Serializable
data class FormattingConfiguration(
    var red: String = "#ff5d52",
    var orange: String = "#ffb026",
    var yellow: String = "#ffdf52",
    var green: String = "#34d944",
    var blue: String = "#7878ff",
    var purple: String = "#8b61ff",
    var pink: String = "#f872fc",
    var gray: String = "#808080",
    var darkgray: String = "#474747",
)

@Serializable
data class ChatConfiguration(
    var enabled: Boolean = true,
    var format: String = "<blue>%username%:</blue> %msg%",
    var bannedWords: List<String> = emptyList(),
    var bannedWordsRegex: List<String> = emptyList()
)

@Serializable
data class TpaskConfiguration(
    var expireTime: Int = 30,
    var teleportWait: Int = 3,
)

@Serializable
data class DiscordConfiguration(
    var enabled: Boolean = false,
    var token: String = "CHANGEME",
    var channelId: String = "CHANGEME",

    var serverStartFormat: String = ":white_check_mark: **Server started**",
    var serverStopFormat: String = ":stop_sign: **Server stopped**",

    var joinFormat: String = ":arrow_right: **%username% joined the server**",
    var leaveFormat: String = ":arrow_left: **%username% left the server**",

    var hytaleToDiscordFormat: String = "**%username%:** %msg%",
    var discordToHytaleFormat: String = "(Discord) %username%: %msg%",
)
