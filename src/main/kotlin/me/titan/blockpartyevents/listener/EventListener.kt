package me.titan.blockpartyevents.listener

import io.papermc.paper.event.player.AsyncChatEvent
import me.titan.blockpartyevents.manager.other.EventChatManager
import me.titan.blockpartyevents.playerManager
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

class EventListener: Listener {
    @EventHandler
    fun onLeave(e: PlayerQuitEvent){
        playerManager().getEvent(e.player)?.leavePlayer(e.player)
    }
    @EventHandler
    fun onDeath(e: PlayerDeathEvent){
        createPE(e,e.player)
    }
    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent){ // I don't like paper components :(

        EventChatManager.onChat(e)
    }
    @EventHandler
    fun onProjectileHit(e: ProjectileHitEvent){
        if(e.entity.shooter !is Player) return
        createPE(e,e.entity.shooter as Player)
    }
    @EventHandler
    fun onInteract(e: PlayerInteractEvent){
        createPE(e,e.player)
    }
    @EventHandler
    fun onDrop(e: PlayerDropItemEvent){
        createPE(e, e.player)
    }
    @EventHandler
    fun onInvClick(e: InventoryClickEvent){
        createPE(e,e.whoClicked as Player)
    }
    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        createPE(e,e.player)
    }
    @EventHandler
    fun onPlace(e: BlockPlaceEvent){
        createPE(e,e.player)
    }

    @EventHandler
    fun onDmg(e: EntityDamageEvent){
        if(e.entity !is Player) return
        createPE(e,e.entity as Player)
    }

    private fun createPE(e: Event, player:Player){
        playerManager().getEvent(player)?.onEvent(e)
    }

}