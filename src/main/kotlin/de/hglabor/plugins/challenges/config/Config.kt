package de.hglabor.plugins.challenges.config

import de.hglabor.plugins.challenges.Challenges
import de.hglabor.plugins.challenges.user.UserList
import net.axay.kspigot.event.listen
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.GameRule
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.player.*
import org.bukkit.event.vehicle.VehicleDamageEvent


object Config {
    init {
        val plugin = Challenges.INSTANCE
        plugin.config.options().copyDefaults(true)
        plugin.saveConfig()
        worldSettings()
        listen<PlayerDropItemEvent> { if (!UserList.getUser(it.player).inChallenge) it.isCancelled = true }
        listen<EntityDamageByEntityEvent> { if (!(it.damager is Player && it.damager.canByPassEvent)) it.isCancelled = true }
        listen<EntityDamageByBlockEvent> { if (!(it.damager is Player && (it.damager as Player).canByPassEvent)) it.isCancelled = true }
        listen<EntityDamageEvent> { if (it.entity is Player && !UserList.getUser(it.entity as Player).inChallenge) it.isCancelled = true }
        listen<BlockBreakEvent> { if (!it.player.canByPassEvent) it.isCancelled = true }
        listen<BlockPlaceEvent> { if (!it.player.canByPassEvent) it.isCancelled = true }
        listen<PlayerJoinEvent> {
            it.joinMessage = null
            it.player.gameMode = GameMode.ADVENTURE
            it.player.walkSpeed = 0.5F
        }
        listen<PlayerQuitEvent> { it.quitMessage = null }
        listen<HangingBreakByEntityEvent> { if (!it.remover?.canByPassEvent!!) it.isCancelled = true }
        listen<VehicleDamageEvent> { if (!it.attacker?.canByPassEvent!!) it.isCancelled = true }
        listen<PlayerInteractEvent> { if (!it.player.canByPassEvent) it.isCancelled = true }
        listen<PlayerInteractEntityEvent> { if (!it.player.canByPassEvent) it.isCancelled = true }
        listen<FoodLevelChangeEvent> { it.isCancelled = true }
    }

    private val Entity.canByPassEvent: Boolean
        get() {
            if (this is Player) return false
            return isOp && (this as Player).gameMode == GameMode.CREATIVE
        }

    private fun worldSettings() {
        val world = Bukkit.getWorld("world")
        world?.spawnLocation = Location(world, 0.0, 78.0, 0.0)
        world?.time = 6000
        world?.isThundering = false
        world?.setStorm(false)
        world?.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world?.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world?.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world?.setGameRule(GameRule.DISABLE_RAIDS, true)
        world?.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
        world?.setGameRule(GameRule.DO_TRADER_SPAWNING, false)
        world?.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)
        world?.setGameRule(GameRule.MOB_GRIEFING, false)
        world?.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
        world?.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world?.setGameRule(GameRule.FALL_DAMAGE, false)
    }

    fun getInteger(key: String) = Challenges.INSTANCE.config.getInt(key)
    fun getDouble(key: String) = Challenges.INSTANCE.config.getDouble(key)
    fun getLong(key: String) = Challenges.INSTANCE.config.getLong(key)
    fun getLocation(key: String) = Challenges.INSTANCE.config.getLocation(key)
}

