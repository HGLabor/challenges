package de.hglabor.plugins.challenges.config

import de.hglabor.plugins.challenges.Challenges

object Config {
    init {
        val plugin = Challenges.INSTANCE
        plugin.config.options().copyDefaults(true)
        plugin.saveConfig()
    }
}
