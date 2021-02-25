package de.hglabor.plugins.challenges.damager

import de.hglabor.plugins.challenges.Challenges
import de.hglabor.plugins.challenges.config.Config
import de.hglabor.plugins.challenges.user.UserList
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent

class Void(name: String) : Damager(name) {
    private var voidStart: Int = 30

    init {
        saveToConfig()
        voidStart = Config.getInteger("${configKey}.voidStart")
        listen<EntityDamageEvent>(priority = EventPriority.MONITOR) {
            if (it.cause == EntityDamageEvent.DamageCause.VOID && it.entity is Player) {
                val user = UserList.getUser(it.entity as Player)
                if (user.inChallenge && user.currentChallenge.challengeName().equals("Void", true)) {
                    it.isCancelled = false
                }
            }
        }
    }

    override fun damageScheduler() {
        task(period = tickSpeed) {
            Bukkit.getOnlinePlayers().forEach {
                if (it.location.y >= voidStart) return@task
                val user = UserList.getUser(it)
                if (user.hasChallengeCompleted) return@task
                if (user.inChallenge) {
                    if (user.soupsEaten == soupsToEat) {
                        complete(it, true)
                        it.teleportAsync(Bukkit.getWorld("world")?.spawnLocation!!).thenAccept { _ ->
                            resetPlayer(it)
                        }
                    }
                } else {
                    setPlayerDamagerReady(it)
                }
            }
        }
    }

    override fun damagePlayer(entity: Player) {
    }

    override fun saveToConfig() {
        val config = Challenges.INSTANCE.config
        config.addDefault("${configKey}.voidStart", voidStart)
        super.saveToConfig()
    }
}
