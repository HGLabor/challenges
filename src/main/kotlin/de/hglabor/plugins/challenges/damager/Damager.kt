package de.hglabor.plugins.challenges.damager

import de.hglabor.plugins.challenges.Challenges
import de.hglabor.plugins.challenges.config.Config
import net.axay.kspigot.extensions.geometry.LocationArea
import net.axay.kspigot.runnables.task
import net.axay.kspigot.structures.entities
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.IOException

class Damager(val name: String) {
    lateinit var area: LocationArea
    private var configKey: String = "Damager.${name}"
    private var damage: Double = 5.0
    private var tickSpeed: Long = 20

    init {
        area.loc1 = Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)
        area.loc2 = Location(Bukkit.getWorld("world"), 0.0, 0.0, 0.0)
        saveToConfig()
        area.loc1 = Config.getLocation("${configKey}.location.first")!!
        area.loc2 = Config.getLocation("${configKey}.location.second")!!
        damage = Config.getInteger("${configKey}.damage")
        tickSpeed = Config.getLong("${configKey}.tickSpeed")
        task(period = tickSpeed) {
            area.entities.forEach { entity ->
                if (entity is Player) {
                    entity.damage(damage)
                }
            }
        }
    }

    private fun saveToConfig() = try {
        val config = Challenges.INSTANCE.config
        config.addDefault("${configKey}.damage", damage)
        config.addDefault("${configKey}.tickSpeed", tickSpeed)
        config.addDefault("${configKey}.location.first", area.loc1)
        config.addDefault("${configKey}.location.second", area.loc2)
        config.options().copyDefaults(true)
        Challenges.INSTANCE.saveConfig()
    } catch (e: IOException) {
        e.printStackTrace();
    }
}
