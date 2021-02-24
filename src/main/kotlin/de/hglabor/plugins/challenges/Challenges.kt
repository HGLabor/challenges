package de.hglabor.plugins.challenges

import de.hglabor.plugins.challenges.command.DamagerCommand
import de.hglabor.plugins.challenges.config.Config
import de.hglabor.plugins.challenges.damager.Damager
import de.hglabor.plugins.challenges.damager.Impossible
import de.hglabor.plugins.challenges.damager.Inconsistency
import de.hglabor.plugins.challenges.listener.SoupHealing
import de.hglabor.plugins.challenges.user.UserList
import dev.jorel.commandapi.CommandAPI
import net.axay.kspigot.main.KSpigot
import org.bukkit.Bukkit

class Challenges : KSpigot() {
    companion object {
        lateinit var INSTANCE: Challenges; private set
    }

    var damagers: MutableList<Damager> = ArrayList()

    override fun startup() {
        Config
        UserList
        Damager.DamagerListener
        val easyDamager = Damager("Easy")
        val mediumDamager = Damager("Medium")
        val hardDamager = Damager("Hard")
        val ultraHardDamager = Impossible("UltraHard")
        val inconsistencyDamager = Inconsistency("Inconsistency")
        damagers.addAll(listOf(easyDamager, mediumDamager, hardDamager, inconsistencyDamager, ultraHardDamager))
        Bukkit.getOnlinePlayers().forEach { player ->
            UserList.getUser(player)
            player.teleport(Bukkit.getWorld("world")?.spawnLocation!!)
        }
        CommandAPI.onEnable(this)
        registerListener()
        registerCommands()
    }

    private fun registerCommands() {
        DamagerCommand()
    }

    private fun registerListener() {
        SoupHealing()
    }

    override fun load() {
        INSTANCE = this
        CommandAPI.onLoad(true)
    }
}
