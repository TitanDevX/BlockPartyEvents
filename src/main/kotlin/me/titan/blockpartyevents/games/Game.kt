package me.titan.blockpartyevents.games

import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.eventManager
import me.titan.blockpartyevents.games.impl.FILGame
import me.titan.blockpartyevents.games.impl.OITCGame
import me.titan.blockpartyevents.games.impl.SGGame
import me.titan.blockpartyevents.games.impl.SpleefGame
import me.titan.blockpartyevents.manager.phase.GamePhaseManager
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.model.event.EventType
import me.titan.blockpartyevents.playerManager
import me.titan.blockpartyevents.plugin
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.scheduler.BukkitRunnable

/**
 * Handles the actual event game logic like floor is lava, survival games, spleef, etc.
 * Anything related to gameplay is done here (in the implementations)
 *
 * also has a game loop every 1 sec
 *
 * If you want to add a new game, make the implementations then modify the static method [Game.createGame]
 */
open class Game(val event: BREvent, val manager: GamePhaseManager, val tickPeriod: Long = 20): BukkitRunnable() {

    var seconds = 0
    protected var winner: Player? = null
    // SETTINGS
    private var deathSystem = false;
    private var noDamage = false;
    private var dropsAllowed = false
    init {

    }
    final override fun run() {

        seconds++
        tick()

    }
    /**
     * Prepare map
     */
    open fun init(){

    }
    open fun start(){
        runTaskTimer(plugin(),20,20)
    }

    /**
     * Called on event-cancel and when it finishes with a winner.
     */
    open fun interrupt(){
        cancel()
    }
    fun finish(winner: Player){
        this.winner = winner
        eventManager().finishEvent(winner)
    }

    fun SetBlock(loc: Location, mat: Material){
        manager.SetBlock(loc,mat)
    }
    open fun tick(){}

    open fun validateConfig(): Boolean{return true}
    open fun onPlayerLeave(player: Player){
        if(event.onlinePlayers.contains(player)){
            event.broadcast(Messages.LEAVE_BROADCAST) {it.replace("%player%",player.name)}
        }
        eliminatePlayer(player,false)
    }
     fun onEvent(e: Event){

         if(deathSystem){
             if(e is PlayerDeathEvent){
                 val victim = e.player
                 val killer = victim.killer
                 if(killer == null){
                     event.broadcast(Messages.PLAYER_DEATH)
                     {it.replace("%victim%",victim.name)}
                 }else{
                     event.broadcast(Messages.PLAYER_DEATH_KILL)
                     {it.replace("%victim%",victim.name)
                         .replace("%killer%",killer.name)}
                 }
                 eliminatePlayer(victim)
                 onDeath(victim,killer)
                 if(!dropsAllowed){
                     e.drops.clear()
                 }
             }
         }else if(noDamage && e is EntityDamageEvent){
             if(e.entity is Player){
                 e.damage = 0.0
             }
         }
         if(!dropsAllowed){
             if(e is PlayerDropItemEvent){
                 e.isCancelled = true
             }else if(e is InventoryClickEvent){
                 if(e.action.name.contains("DROP")){
                     e.isCancelled = true
                 }
             }
         }

         handleEvent(e)
    }

    open fun handleEvent(e: Event){

    }
    open fun onDeath(victim: Player, killer: Player? = null){}
    fun useDeathSystem(){
        deathSystem = true
    }
    fun useNoDamage(){
        noDamage = true
    }
    fun useDropsAllowed(){
        dropsAllowed = true
    }
    open fun eliminatePlayer(player: Player, makeSpectator: Boolean = true){
        event.onlinePlayers.remove(player)
        if(makeSpectator){
            playerManager().makeSpectator(player)
            event.spectators.add(player)
        }

        if(event.onlinePlayers.size == 1){
            finish(event.onlinePlayers.first())
        }
    }

    companion object {
        fun createGame(event: BREvent, manager: GamePhaseManager): Game {
            if(event.type == EventType.FIL) return FILGame(event, manager)
            if(event.type == EventType.SG) return SGGame(event, manager)
            if(event.type == EventType.OITC) return OITCGame(event, manager)
            if(event.type == EventType.SPLEEF) return SpleefGame(event, manager)
            return FILGame(event, manager) // TODO
        }
    }

}