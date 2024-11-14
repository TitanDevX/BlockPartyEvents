package me.titan.blockpartyevents.listener

import me.titan.blockpartyevents.cache.PlayerCache
import me.titan.blockpartyevents.manager.other.EventChatManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class OutEventListener: Listener {

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent){

        EventChatManager.onChat(e)

    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent){

        PlayerCache.players.remove(e.player.uniqueId)

    }

}