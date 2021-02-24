package de.hglabor.plugins.challenges.config

import de.hglabor.plugins.challenges.Challenges
import de.hglabor.plugins.challenges.user.UserList
import net.axay.kspigot.event.listen
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.GameRule
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.vehicle.VehicleDamageEvent

object Config {
    init {
        val plugin = Challenges.INSTANCE
        plugin.config.options().copyDefaults(true)
        plugin.saveConfig()
        worldSettings()
        listen<FoodLevelChangeEvent> { it.isCancelled = true }
        listen<PlayerDropItemEvent> { if (!UserList.getUser(it.player).inChallenge) it.isCancelled = true }
        listen<EntityDamageByEntityEvent> { if (!(it.damager is Player && isOp(it.damager as Player))) it.isCancelled = true }
        listen<BlockBreakEvent> { if (!isOp(it.player)) it.isCancelled = true }
        listen<BlockPlaceEvent> { if (!isOp(it.player)) it.isCancelled = true }
        listen<PlayerJoinEvent> { it.joinMessage = null }
        listen<PlayerQuitEvent> { it.quitMessage = null }
        listen<HangingBreakByEntityEvent> { it.isCancelled = true }
        listen<VehicleDamageEvent> { it.isCancelled = true }
    }

    private fun isOp(player: Player) = player.isOp && player.gameMode == GameMode.CREATIVE

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

