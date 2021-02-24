package de.hglabor.plugins.challenges.damager

import de.hglabor.plugins.challenges.Challenge
import de.hglabor.plugins.challenges.Challenges
import de.hglabor.plugins.challenges.config.Config
import de.hglabor.plugins.challenges.user.UserList
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.geometry.LocationArea
import net.axay.kspigot.runnables.task
import net.axay.kspigot.structures.entities
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftItem
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.ItemStack

class Damager(val name: String) : Challenge {
    object DamagerListener {
        init {
            listen<PlayerAttemptPickupItemEvent> { if (it.item.owner != it.player.uniqueId) it.isCancelled = true }
            listen<PlayerRespawnEvent> { it.respawnLocation = Bukkit.getWorld("world")?.spawnLocation!! }
            listen<PlayerDropItemEvent> {
                if (it.isCancelled) return@listen
                val user = UserList.getUser(it.player)
                if (user.currentChallenge !is Damager) return@listen
                it.itemDrop.owner = it.player.uniqueId
                if (it.itemDrop.itemStack.type == Material.MUSHROOM_STEW) {
                    user.soupsDropped++
                }
            }
            listen<PlayerDeathEvent> { event ->
                event.drops.clear()
                val user = UserList.getUser(event.entity)
                if (user.currentChallenge !is Damager) return@listen
                val damager = user.currentChallenge as Damager
                damager.complete(event.entity, false)
                if (user.inChallenge) {
                    damager.resetPlayer(event.entity)
                }
            }
        }
    }

    var area: LocationArea
    private var configKey: String = "Damager.${name}"
    private var damage: Double = 5.0
    private var tickSpeed: Long = 20
    private var soupsToEat: Int = 64

    init {
        val dummyLoc = Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)
        area = LocationArea(dummyLoc, dummyLoc)
        saveToConfig()
        area.loc1 = Config.getLocation("${configKey}.location.first")!!
        area.loc2 = Config.getLocation("${configKey}.location.second")!!
        damage = Config.getDouble("${configKey}.damage")
        tickSpeed = Config.getLong("${configKey}.tickSpeed")
        soupsToEat = Config.getInteger("${configKey}.soupsToEat")
        damageScheduler()
    }

    private fun damageScheduler() {
        task(period = tickSpeed) {
            area.entities.forEach { entity ->
                if (entity is Player) {
                    val user = UserList.getUser(entity)
                    if (user.hasChallengeCompleted) return@task
                    if (user.inChallenge) {
                        if (user.soupsEaten == soupsToEat) {
                            complete(entity, true)
                            entity.teleportAsync(Bukkit.getWorld("world")?.spawnLocation!!).thenAccept {
                                resetPlayer(entity)
                            }
                        } else {
                            damagePlayer(entity)
                        }
                    } else {
                        setPlayerDamagerReady(entity)
                    }
                }
            }
        }
    }

    private fun resetPlayer(player: Player) {
        val user = UserList.getUser(player)
        player.closeInventory()
        player.inventory.clear()
        user.inChallenge = false
        user.hasChallengeCompleted = false
        user.soupsEaten = 0
        user.soupsDropped = 0
    }

    private fun complete(player: Player, hasCompleted: Boolean) {
        val user = UserList.getUser(player)
        user.hasChallengeCompleted = hasCompleted
        if (hasCompleted)
            player.sendMessage("${KColors.BLUE}you've completed the damager")
        else
            player.sendMessage("${KColors.RED}you've FAILED the damager")
        player.sendMessage("Soups eaten: ${user.soupsEaten}")
        player.sendMessage("Soups dropped: ${user.soupsDropped}")
        task(delay = 1) {
            this.area.entities.forEach {
                if (it is CraftItem && it.owner == player.uniqueId) {
                    it.itemStack.amount = 0
                }
            }
        }
    }

    private fun damagePlayer(entity: Player) {
        entity.damage(damage)
    }

    private fun setPlayerDamagerReady(player: Player) {
        val user = UserList.getUser(player)
        user.inChallenge = true
        user.currentChallenge = this
        player.inventory.clear()
        player.inventory.addItem(ItemStack(Material.STONE_SWORD))
        var size = 36
        size--
        if (soupsToEat > size) {
            val soupsLeft = soupsToEat - size
            player.inventory.setItem(13, ItemStack(Material.BOWL, soupsLeft))
            player.inventory.setItem(14, ItemStack(Material.RED_MUSHROOM, soupsLeft))
            player.inventory.setItem(15, ItemStack(Material.BROWN_MUSHROOM, soupsLeft))
        }
        for (i in 1..size) {
            player.inventory.addItem(ItemStack(Material.MUSHROOM_STEW))
        }
        damagePlayer(player)
    }

    fun saveLocations(loc1: Location, loc2: Location) {
        area.loc1 = loc1
        area.loc2 = loc2
        val config = Challenges.INSTANCE.config
        config.set("${configKey}.location.first", area.loc1)
        config.set("${configKey}.location.second", area.loc2)
        Challenges.INSTANCE.saveConfig()
    }

    private fun saveToConfig() {
        val config = Challenges.INSTANCE.config
        config.addDefault("${configKey}.damage", damage)
        config.addDefault("${configKey}.soupsToEat", soupsToEat)
        config.addDefault("${configKey}.tickSpeed", tickSpeed)
        config.addDefault("${configKey}.location.first", area.loc1)
        config.addDefault("${configKey}.location.second", area.loc2)
        config.options().copyDefaults(true)
        Challenges.INSTANCE.saveConfig()
    }

    override fun challengeName() = name
}
