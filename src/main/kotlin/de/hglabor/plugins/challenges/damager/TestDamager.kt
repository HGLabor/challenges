package de.hglabor.plugins.challenges.damager

import de.hglabor.plugins.challenges.user.UserList
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.task
import org.bukkit.event.player.PlayerInteractEvent

object TestDamager {
    init {
        listen<PlayerInteractEvent> { event ->
            val player = event.player
            val user = UserList.getUser(player)
            if (!user.inChallenge) {
                user.inChallenge = true
                task(period = 20) {
                    if (player.isDead) {
                        it.cancel()
                    } else {
                        player.damage(5.0);
                    }
                }
            }
        }
    }
}
