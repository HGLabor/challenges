package de.hglabor.plugins.challenges.user

import net.axay.kspigot.event.listen
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import kotlin.collections.HashMap

object UserList {
    private val users: MutableMap<UUID, User> = HashMap()

    init {
        listen<PlayerJoinEvent> { event ->
            users.computeIfAbsent(event.player.uniqueId) { User(event.player.uniqueId, event.player.name) }
        }
        listen<PlayerQuitEvent> {
            users.remove(it.player.uniqueId)
        }
    }

    fun getUser(player: Player) = users.computeIfAbsent(player.uniqueId) { User(player.uniqueId, player.name) }
}
