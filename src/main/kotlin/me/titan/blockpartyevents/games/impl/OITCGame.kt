package me.titan.blockpartyevents.games.impl

import de.tr7zw.changeme.nbtapi.NBT
import me.titan.blockpartyevents.api.event.EventCancelCause
import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.config.model.types.OITCEventConfig
import me.titan.blockpartyevents.eventManager
import me.titan.blockpartyevents.games.Game
import me.titan.blockpartyevents.mainConfig
import me.titan.blockpartyevents.manager.phase.GamePhaseManager
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

class OITCGame(event: BREvent, manager: GamePhaseManager): Game(event, manager) {



    val playerData = HashMap<String, OITCPlayerData>()

    init {
    }
    override fun init() {

    useDeathSystem()
    }


    override fun start() {
        super.start()
        var i = 0
        for (p in event.onlinePlayers) {

            var loc: Location? = null
            if(event.info.spawns.containsKey("spawn_${++i}")){
                loc = event.info.spawns.get("spawn_${i}")
                playerData[p.name] = OITCPlayerData(getConfig().lives!!,loc!!)
                getConfig().swordItem!!.let {
                    p.inventory.setItem(it.slot,setItemMeta(it.item,"sword"))
                }
                getConfig().bowItem!!.let {
                    p.inventory.setItem(it.slot,setItemMeta(it.item,"bow"))
                }
                getConfig().arrowItem!!.let {
                    p.inventory.setItem(it.slot,setItemMeta(it.item,"arrow"))
                }
            }else{

                // WTF??
                plugin().logger.severe("Error with oitc game, couldn't find spawn point spawn_$i, WTF")
                eventManager().cancelEvent(EventCancelCause.ERROR)
            }


        }
    }

    override fun tick() {



    }


    override fun handleEvent(e: Event) {
    if(e is ProjectileHitEvent){

        if(e.entity is Arrow && e.entity.shooter is Player){
            val p = e.entity.shooter!! as Player
            if(playerData.containsKey(p.name)) {
                e.entity.remove()
                if (e.hitEntity != null) {
                    if (e.hitEntity is Player && playerData.containsKey(e.hitEntity!!.name)) {
                        val hitPlayer = e.hitEntity as Player
                        if (hitPlayer.name == p.name) return // check if player is trying to hit himself


                        Bukkit.getScheduler().runTaskLater(plugin(), { ep ->
                            hitPlayer.killer = p;
                            hitPlayer.health = 0.0;
                            p.inventory.addItem(getConfig().arrowItem!!.item)
                        }, 1);

                    }
                }
            }

        }
    }else if(e is EntityDamageByEntityEvent){
        if(e.damager is Arrow){
            e.damage = 0.0
        }
    }else if(e is PlayerDeathEvent){
        e.keepInventory = true
    }

    }

    override fun eliminatePlayer(player: Player, makeSpectator: Boolean) {


        val pd = playerData.get(player.name)!!
        if(--pd.lives > 0){
            Messages.OITC_PLAYER_RESPAWN.tell(player,"%lives%",pd.lives.toString())

            Bukkit.getScheduler().runTaskLater(plugin(), { ee ->
                player.spigot().respawn()
                player.teleport(pd.spawn)
            },40)
            return;
        }else{
            Messages.OITC_NO_LIVES.tell(player)
        }
        playerData.remove(player.name)
        super.eliminatePlayer(player,makeSpectator)


    }



    override fun validateConfig(): Boolean {

        val b = arrayOf(getConfig().lives != null,
            getConfig().bowItem != null,
            getConfig().arrowItem != null,
            getConfig().swordItem != null)
        if(b.all { it }) return true
        for(i in b.indices){
            if(i == 0){
                plugin().logger.severe("Please set lives for OITC event in config. the event will be cancelled.")

            }else if(i == 1){
                plugin().logger.severe("Please set bow item for OITC event in config. the event will be cancelled.")

            }else if(i == 2){
                plugin().logger.severe("Please set arrow item for OITC event in config. the event will be cancelled.")

            }else if(i == 3){
                plugin().logger.severe("Please set sword item for OITC event in config. the event will be cancelled.")
            }
        }
        return false
    }
    fun getConfig():  OITCEventConfig {
        return mainConfig().OITCEventConfig
    }
    private fun setItemMeta(item: ItemStack, id: String): ItemStack{

    NBT.modify(item) {
        it.setString("OITCGAMEITEM",id)
        }
    return item
    }

    override fun toString(): String {
        return "OITCGame(playerData=$playerData)"
    }

}
class OITCPlayerData(var lives: Int, val spawn: Location) {
    var arrow: Arrow? = null

}