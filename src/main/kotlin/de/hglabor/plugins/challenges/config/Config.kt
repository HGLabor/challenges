package de.hglabor.plugins.challenges.config

import de.hglabor.plugins.challenges.Challenges
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.PlayerCommandExecutor

object Config {
    init {
        val plugin = Challenges.INSTANCE
        plugin.config.options().copyDefaults(true)
        plugin.saveConfig()
    }

    fun getInteger(key: String) = Challenges.INSTANCE.config.getDouble(key)
    fun getLong(key: String) = Challenges.INSTANCE.config.getLong(key)
    fun getLocation(key: String) = Challenges.INSTANCE.config.getLocation(key)

}

