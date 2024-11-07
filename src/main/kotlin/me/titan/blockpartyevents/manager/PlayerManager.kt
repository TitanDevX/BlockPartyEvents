package me.titan.blockpartyevents.manager

import me.titan.blockpartyevents.model.event.BREvent
import me.titan.titanlib.common.MetaManager
import org.bukkit.GameMode
import org.bukkit.entity.Player

class PlayerManager {


    fun resetPlayer(player: Player){
        teleportSpawn(player)
        removeEventMetadata(player)
    }
    fun teleportSpawn(player: Player){
        // TODO
    }
    fun setEventMetadata(player: Player, event: BREvent){
        MetaManager.setMetadataObject(player, PLAYER_EVENT_METADATA,event)
    }
    fun removeEventMetadata(player: Player){
        if(!player.hasMetadata(PLAYER_EVENT_METADATA)) return
        MetaManager.removeMetadata(player, PLAYER_EVENT_METADATA)
    }
    fun getEvent(player: Player): BREvent? {
        if(!player.hasMetadata(PLAYER_EVENT_METADATA)) return null
        return MetaManager.getMetadataObject(player, PLAYER_EVENT_METADATA) as BREvent
    }
    fun makeSpectator(player: Player){

        player.inventory.clear()
        player.gameMode = GameMode.SPECTATOR
    }

    companion object{
        val PLAYER_EVENT_METADATA = "IN_A_BLOCKPARTY_EVENT[td]"
    }

}