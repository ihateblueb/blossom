package site.remlit.orchidcore.service

import java.util.UUID

object BackService {
    /**
     * Last deaths of players, first is the player, second is location of death
     * */
    val lastDeath = mutableMapOf<UUID, String>()
}