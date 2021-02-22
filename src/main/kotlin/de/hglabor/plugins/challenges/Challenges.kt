package de.hglabor.plugins.challenges

import de.hglabor.plugins.challenges.command.DamagerCommand
import de.hglabor.plugins.challenges.config.Config
import de.hglabor.plugins.challenges.damager.Damager
import de.hglabor.plugins.challenges.user.UserList
import net.axay.kspigot.main.KSpigot

class Challenges : KSpigot() {
    companion object {
        lateinit var INSTANCE: Challenges; private set
    }

    var damagers: MutableList<Damager> = ArrayList()

    override fun startup() {
        Config
        UserList
        val easyDamager = Damager("Easy Damager")
        val mediumDamager = Damager("Medium Damager")
        val hardDamager = Damager("Hard Damager")
        damagers.addAll(listOf(easyDamager, mediumDamager, hardDamager))
    }

    private fun registerCommands() {
        DamagerCommand()
    }

    override fun load() {
        INSTANCE = this
    }
}
