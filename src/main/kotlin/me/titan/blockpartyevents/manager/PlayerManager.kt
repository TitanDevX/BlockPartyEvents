package me.titan.blockpartyevents.manager

import me.titan.blockpartyevents.cache.PlayerCache
import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.mainConfig
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.plugin
import me.titan.titanlib.common.Common
import me.titan.titanlib.common.MetaManager
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class PlayerManager {



    fun resetPlayer(player: Player){
        removeEventMetadata(player)
        setSpawningMetadata(player)
        teleportSpawn(player)

    }

    fun handlePlayerLeaveNotInEvent(player: Player){

        if(!player.hasMetadata(PLAYER_SPAWN_METADATA)){
            Messages.LEAVE_FAIL_NO_EVENT.tell(player)
            return;
        }
        Common.tell(player,"&cPlease wait, you are being teleported.")
    }
    /**
     * Teleport player to last location he was at before he joined the event.
     *
     * in case of an error or for some reason player's last location is not set in the database, this uses config's spawn_command.
     */
    fun teleportSpawn(player: Player){

        Messages.TELEPORTING_TO_SPAWN.tell(player)
        PlayerCache.getOrLoadPlayerCache(player.uniqueId).callAsync().whenComplete { t, u ->
            if(u != null || t.lastLoc == null){
                Bukkit.getScheduler().runTask(plugin(), Runnable {
                    teleportToFixedSpawn(player)
                    player.removeMetadata(PLAYER_SPAWN_METADATA, plugin())
                    Common.tell(player,"&cThere was an error teleporting you to your last location, so you were teleported to spawn.")
                })
            }else {
                player.teleport(t.lastLoc!!)
                PlayerCache.updateLastLoc(player.uniqueId, null)
                Bukkit.getScheduler().runTask(plugin(), Runnable {
                    player.removeMetadata(PLAYER_SPAWN_METADATA, plugin())

                })

            }
        }
    }
    private fun teleportToFixedSpawn(player: Player){
       mainConfig().getSpawnCommandTemplate().let {
           Bukkit.dispatchCommand(Bukkit.getConsoleSender(),it.replace("%player%",player.name))
       }
    }
    fun teleportToEvent(player: Player, tp: Location): CompletableFuture<Void>{

        val f =   PlayerCache.updateLastLoc(player.uniqueId,player.location)
      f.whenComplete {v, th ->
            if(th != null) {
                plugin().logger.warning("Unable to upadete player's last loc (p name: ${player.name}):")
                th.printStackTrace()
            }else{

                Bukkit.getScheduler().runTask(plugin(), Runnable {
                    player.teleport(tp)
                })
            }


        }
        return f


    }
    private fun setSpawningMetadata(player: Player){

        MetaManager.setMetadataObject(player, PLAYER_SPAWN_METADATA, true)
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

        /**
         * This metadata is set while player is being teleported to spawn or last location.
         */
        val PLAYER_SPAWN_METADATA = "IN_A_BLOCKPARTY_SPAWN[td]"
    }

}