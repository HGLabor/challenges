package de.hglabor.plugins.challenges.damager

import org.bukkit.entity.Player

class Impossible(name: String) : Damager(name) {

    override fun setPlayerDamagerReady(player: Player) {
        player.maximumNoDamageTicks = 0
        super.setPlayerDamagerReady(player)
    }

    override fun resetPlayer(player: Player) {
        player.maximumNoDamageTicks = 20
        super.resetPlayer(player)
    }
}
