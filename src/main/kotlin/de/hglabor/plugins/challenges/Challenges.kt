package de.hglabor.plugins.challenges

import de.hglabor.plugins.challenges.config.Config
import de.hglabor.plugins.challenges.damager.TestDamager
import de.hglabor.plugins.challenges.user.UserList
import net.axay.kspigot.main.KSpigot

class Challenges : KSpigot() {
    companion object {
        lateinit var INSTANCE: Challenges; private set
    }

    override fun startup() {
        Config
        UserList
        TestDamager
    }

    override fun load() {
        INSTANCE = this
    }
}
