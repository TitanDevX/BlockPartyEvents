package me.titan.blockpartyevents.manager.phase

import me.titan.blockpartyevents.api.event.EventCancelCause
import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.eventManager
import me.titan.blockpartyevents.mainConfig
import me.titan.blockpartyevents.manager.other.EventFinisher
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.model.event.EventState
import me.titan.blockpartyevents.playerManager
import me.titan.blockpartyevents.plugin
import me.titan.blockpartyevents.tasks.PreLobbyCountdown
import me.titan.titanlib.common.Common
import me.titan.titanlib.common.TimeUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.scheduler.BukkitTask

class FinishedPhaseManager(event: BREvent): EventPhaseManager(event) {

    var endTask: BukkitTask? = null
    var winner: Player? = null
    var onFinishFunc: Runnable =Runnable {  }
    override fun startState(): EventState {
        return EventState.FINISHED
    }

    override fun finishState(): EventState {

        return EventState.IDLE // fake
    }

    override fun check(vararg params: Any): Boolean {

        return true
    }

    override fun onEvent(e: Event) {
        super.onEvent(e)

    }

    override fun startNoChecks(vararg params: Any) {

        if(params.isEmpty()) return
         winner =  params[0] as Player
        if(params.size == 2){
            onFinishFunc = params[1] as Runnable;
        }

        // cancel tasks related to specific game.
        event.getGamePhaseManager().onGameFinish(3)
        // run event finisher which: gives reward, runs events and sends notifications.
        val finisher = EventFinisher(event,winner!!)
        finisher.start()

        if(mainConfig().eventFinishPlayersSpawnDelay == null){
            finalOnFinish()
            return
        }
        val v = mainConfig().eventFinishPlayersSpawnDelay!! * 0.02
        endTask = Bukkit.getScheduler().runTaskLater(plugin(), Runnable {
            finalOnFinish()
        }, v.toLong())

        // why clear maps if the event object is not going to be accessible anyway?
        //event.spectators.clear()
        // event.onlinePlayers.clear()

    }

    private fun finalOnFinish(){

        // teleports players to spawn and removes event metadata.
        for (op in event.onlinePlayers) {
            Messages.EVENT_FINISH_PLAYERS_AFTER_DELAY.tell(op,"%event%",event.getConfig().humanName)
            playerManager().resetPlayer(op)
        }
        for (spectator in event.spectators) {
            Messages.EVENT_FINISH_PLAYERS_AFTER_DELAY.tell(spectator,"%event%",event.getConfig().humanName)
            playerManager().resetPlayer(spectator)
        }

        // calls game manager finish handle.
        event.getGamePhaseManager().onGameFinish(1)


        onFinishFunc.run()
    }
    override fun cancel(cause: EventCancelCause) {
        try{
            endTask!!.cancel()
        }catch (_: Throwable){

        }
        finalOnFinish()

    }


    override fun joinPlayer(player: Player): Boolean {
        Messages.JOIN_FAIL_ALREADY_STARTED.tell(player)
        return false
    }

    override fun toString(): String {
        return "FinishedPhaseManager()"
    }


}