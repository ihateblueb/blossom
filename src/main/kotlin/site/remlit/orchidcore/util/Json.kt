package site.remlit.orchidcore.util

import kotlinx.serialization.json.Json

val jsonConfig = Json {
    encodeDefaults = true
    prettyPrint = true
    isLenient = true
    explicitNulls = false
    ignoreUnknownKeys = true
}