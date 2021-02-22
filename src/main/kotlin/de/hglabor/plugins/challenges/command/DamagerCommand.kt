package de.hglabor.plugins.challenges.command

import de.hglabor.plugins.challenges.Challenges.Companion.INSTANCE
import de.hglabor.plugins.challenges.damager.Damager
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import org.bukkit.Location
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap

class DamagerCommand {
    data class DamagerContainer(var firstLoc: Location, var secondLoc: Location, val damager: Damager)

    private var selectors: MutableMap<UUID, DamagerContainer> = HashMap()

    init {
        CommandAPICommand("damager")
                .withPermission("group.admin")
                .withArguments(damagerArgument())
                .withArguments(GreedyStringArgument("action").overrideSuggestions("relocate", "confirm"))
                .executesPlayer(PlayerCommandExecutor { player, arrayOfAnys ->
                    when ((arrayOfAnys[1] as String).toLowerCase()) {
                        "relocate" -> {
                            val damager = arrayOfAnys[0] as Damager
                            player.sendMessage("${KColors.ALICEBLUE}selected damager: $damager")
                            player.sendMessage("${KColors.ALICEBLUE}Leftclick = first location")
                            player.sendMessage("${KColors.ALICEBLUE}Rightclick = second location")
                            selectors[player.uniqueId] = DamagerContainer(damager.area.loc1, damager.area.loc2, damager)
                        }
                        "confirm" -> {
                            if (!selectors.containsKey(player.uniqueId)) player.sendMessage("${KColors.RED}not in selection mode!")
                            val damager = selectors[player.uniqueId]?.damager
                            val firstLoc = selectors[player.uniqueId]?.firstLoc!!
                            val secondLoc = selectors[player.uniqueId]?.secondLoc!!
                            damager?.area?.loc1 = firstLoc
                            damager?.area?.loc2 = secondLoc
                            player.sendMessage("${damager?.name} successfully relocated 1. $firstLoc + 2. $secondLoc")
                            selectors.remove(player.uniqueId)
                        }
                    }
                })
                .register()
        listen<BlockBreakEvent> {
            val damagerInfo = selectors[it.player.uniqueId]
            damagerInfo?.firstLoc = it.block.location
            it.player.sendMessage("First position set at ${it.block.location}")
        }
        listen<PlayerInteractEvent> {
            if (it.hasBlock() && it.action == Action.RIGHT_CLICK_BLOCK) {
                val damagerInfo = selectors[it.player.uniqueId]
                damagerInfo?.secondLoc = it.clickedBlock?.location!!
                it.player.sendMessage("Second position set at ${it.clickedBlock?.location}")
            }
        }
    }

    private fun damagerArgument() = CustomArgument("damager") { s: String? ->
        INSTANCE.damagers
                .stream()
                .filter { damager: Damager -> damager.name.equals(s, ignoreCase = true) }
                .findFirst().orElse(null)
    }.overrideSuggestions(INSTANCE.damagers.stream().map(Damager::name).collect(Collectors.toList()))
}