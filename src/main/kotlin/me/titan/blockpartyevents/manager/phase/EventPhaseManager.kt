package me.titan.blockpartyevents.manager.phase

import me.titan.blockpartyevents.api.event.EventCancelCause
import me.titan.blockpartyevents.api.event.EventPhaseStart
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.model.event.EventState
import me.titan.blockpartyevents.util.CallEvent
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

abstract class EventPhaseManager(val event: BREvent) {

    /**
     * The inital state of the phasse
     */
    abstract fun startState(): EventState

    /**
     * The state that
     */
    abstract fun finishState(): EventState
    abstract fun check(vararg params: Any): Boolean
    abstract fun startNoChecks(vararg params: Any)
    fun start(vararg params: Any){
        if(check(*params) && event.state == startState()){
            startNoChecks(*params)
        }
    }

    fun startNext() {

        event.state = finishState()

        val np = event.getNextPhaseManager()

        CallEvent(EventPhaseStart(event, np)).let {
            if (!it) {
                event.currentPhase = np
                np.start()
            }
        }


    }

    open fun joinPlayer(player: Player): Boolean {
        return false
    }
    open fun allowLeavePlayer(player: Player): Boolean {
        return true
    }
    open fun leavePlayer(player: Player) {}
    open fun getRemainingCountdown(): String? = null

    open fun  onEvent(e: org.bukkit.event.Event){
        if(e is PlayerTeleportEvent){
            // check if player teleported to another world, remove him from the event.
            if(!e.to.world.equals(e.from.world)){
                event.leavePlayer(e.player)
            }
        }
    }
    open fun cancel(cause: EventCancelCause){}



}