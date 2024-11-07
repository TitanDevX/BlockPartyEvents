package me.titan.blockpartyevents.manager

import me.titan.blockpartyevents.api.event.EventCancelCause
import me.titan.blockpartyevents.api.event.EventCancelEvent
import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.manager.other.EventFinisher
import me.titan.blockpartyevents.model.event.*
import me.titan.blockpartyevents.playerManager
import me.titan.blockpartyevents.runSync
import me.titan.blockpartyevents.util.CallEvent
import me.titan.blockpartyevents.util.tell
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.lang.IllegalStateException

class EventManager {

    var currentEvent: BREvent? = null
        private set(value) {
            field = value
        }


    fun startEvent(s: CommandSender,type: EventType) {

        if(isAnEventRunning()){
            s.tell("&cAn event is already running, wait for it to finish or do /event cancel to cancel.")
            //return;
        }
        val info = EventInfo(s.name,type)
        s.tell("&aLoading event info (spawns).")
        info.load().whenComplete { t, u ->


            if(u != null){
                s.tell("&cAn error while creating event, please view console for more info.")
                u.printStackTrace()
                return@whenComplete
            }
            {
                s.tell("&aLoaded event info, now creating the event.")
                val event = BREvent(type, info)
                event.state = EventState.PRE_LOBBY
                currentEvent = event
                event.currentPhase = event.getNextPhaseManager()
                event.currentPhase!!.start() // start the pre lobby phase
            }.runSync()
        }



    }
    fun cancelEvent(cause: EventCancelCause): Boolean{
        if(!isAnEventRunning()) return false
        CallEvent(EventCancelEvent(currentEvent!!,cause)).let {
            if(it && cause != EventCancelCause.ERROR) return false
            // cancel event
            currentEvent!!.cancel(cause)
            currentEvent = null
           return true
        }
    }
    fun finishEvent(winner: Player){
        if(currentEvent == null){

            throw IllegalStateException("There is no event currently running.")
        }
        val event = currentEvent!!;
        event.state = EventState.FINISHED
        event.getNextPhaseManager().start(winner , Runnable {
            currentEvent = null // onFinishFunc
        })

    }
    fun joinEvent(player: Player): Boolean {
        if(!isAnEventRunning()){
            Messages.JOIN_FAIL_NO_EVENT.tell(player)
            return false;
        }

        return currentEvent!!.joinPlayer(player)
    }
    fun leaveEvent(player: Player): Boolean{
        if(!isAnEventRunning()){
            Messages.LEAVE_FAIL_NO_EVENT.tell(player)
            return false;
        }
        return currentEvent!!.leavePlayer(player)
    }
    fun isAnEventRunning(): Boolean{
        return currentEvent != null && currentEvent?.state != EventState.IDLE
    }

}