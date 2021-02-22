package de.hglabor.plugins.challenges.listener

import de.hglabor.plugins.challenges.user.UserList
import net.axay.kspigot.event.listen
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import kotlin.math.min

class SoupHealing {
    private val soupMaterial: MutableList<Material> = mutableListOf(Material.MUSHROOM_STEW, Material.SUSPICIOUS_STEW)

    init {
        listen<PlayerInteractEvent> {
            val player = it.player
            val user = UserList.getUser(player)
            if (it.action == Action.LEFT_CLICK_AIR) {
                return@listen
            }
            it.item ?: return@listen
            if (it.hasItem() && soupMaterial.contains(it.material)) {
                if (it.hand == EquipmentSlot.OFF_HAND) {
                    return@listen
                }
                val amountToHeal = 7
                if (player.health < player.maxHealth) {
                    player.health = min(player.health + amountToHeal, player.maxHealth)
                    player.inventory.setItemInMainHand(ItemStack(Material.BOWL))
                } else if (player.foodLevel < 20) {
                    player.foodLevel = player.foodLevel + 6
                    player.saturation = player.saturation + 7
                    player.inventory.setItemInMainHand(ItemStack(Material.BOWL))
                }
                user.soupsEaten++
            }
        }
    }
}
