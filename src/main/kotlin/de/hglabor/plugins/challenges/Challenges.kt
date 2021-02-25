package de.hglabor.plugins.challenges

import de.hglabor.plugins.challenges.command.DamagerCommand
import de.hglabor.plugins.challenges.config.Config
import de.hglabor.plugins.challenges.damager.Damager
import de.hglabor.plugins.challenges.damager.Impossible
import de.hglabor.plugins.challenges.damager.Inconsistency
import de.hglabor.plugins.challenges.damager.Void
import de.hglabor.plugins.challenges.listener.SoupHealing
import de.hglabor.plugins.challenges.user.UserList
import de.hglabor.utils.noriskutils.listener.RemoveHitCooldown
import dev.jorel.commandapi.CommandAPI
import net.axay.kspigot.main.KSpigot
import net.axay.kspigot.runnables.task
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
        val voidDamager = Void("Void")
        damagers.addAll(listOf(easyDamager, mediumDamager, hardDamager, inconsistencyDamager, ultraHardDamager, voidDamager))
        Bukkit.getOnlinePlayers().forEach { player ->
            UserList.getUser(player)
            player.walkSpeed = 0.5F
            player.teleport(Bukkit.getWorld("world")?.spawnLocation!!)
        }
        CommandAPI.onEnable(this)
        registerListener()
        registerCommands()
        //Ganz traurig HAHAHA
        task(period = 20 * 60 * 1) {
            if (Bukkit.getOnlinePlayers().isNotEmpty()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "holograms reload")
        }
    }

    private fun registerCommands() {
        DamagerCommand()
    }

    private fun registerListener() {
        SoupHealing()
        Bukkit.getPluginManager().registerEvents(RemoveHitCooldown(), this)
    }

    override fun load() {
        INSTANCE = this
        CommandAPI.onLoad(true)
    }
}
