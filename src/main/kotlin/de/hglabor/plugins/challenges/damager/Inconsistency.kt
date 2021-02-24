package de.hglabor.plugins.challenges.damager

import de.hglabor.plugins.challenges.Challenges
import de.hglabor.plugins.challenges.config.Config
import de.hglabor.utils.noriskutils.ChanceUtils
import org.bukkit.entity.Player

class Inconsistency(name: String) : Damager(name) {
    private var minDamage: Double = 4.0

    init {
        saveToConfig()
        minDamage = Config.getDouble("${configKey}.minDamage")
    }

    override fun damagePlayer(entity: Player) {
        entity.damage(ChanceUtils.getRandomDouble(minDamage, damage))
    }

    override fun saveToConfig() {
        val config = Challenges.INSTANCE.config
        config.addDefault("${configKey}.minDamage", minDamage)
        super.saveToConfig()
    }
}
