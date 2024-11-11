package me.titan.blockpartyevents.manager.phase

import me.titan.blockpartyevents.api.event.EventCancelCause
import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.model.event.EventState
import me.titan.blockpartyevents.playerManager
import me.titan.blockpartyevents.tasks.PreLobbyCountdown
import me.titan.titanlib.common.Common
import me.titan.titanlib.common.TimeUtil
import org.bukkit.entity.Player
import org.bukkit.event.Event

class PreLobbyManager(event: BREvent): EventPhaseManager(event) {
     var countdown: PreLobbyCountdown? = null
    override fun startState(): EventState {
        return EventState.PRE_LOBBY
    }

    override fun finishState(): EventState {

        return EventState.LOBBY
    }

    override fun check(vararg params: Any): Boolean {

        return event.validateSpawns() && event.validateConfig()
    }

    override fun onEvent(e: Event) {
        super.onEvent(e)

    }

    override fun startNoChecks(vararg params: Any) {
        countdown = object : PreLobbyCountdown(event) {
            override fun finish() {
                startNext()
            }


        }
    }

    override fun cancel(cause: EventCancelCause) {
        countdown?.cancel()
    }

    override fun getRemainingCountdown(): String? {
        return countdown?.let {
            TimeUtil.formatTimeShort((it.cooldown-it.seconds).toLong())
        }
    }
    override fun joinPlayer(player: Player): Boolean {
        val max = event.getConfig().basicSettings.playersAmount.max
        if(event.onlinePlayers.size >= max){
            Messages.JOIN_FAIL_FULL.tell(player)
            return false;
        }
        player.inventory.clear()
        playerManager().teleportToEvent(player,event.info.spawns.get("global_lobby")!!).whenComplete { t, u ->
            if(u != null){
                event.leavePlayer(player)
                Common.tell(player,"&cThere was an error teleporting you to event so you were removed from it.")

            }
           }
        return true
    }

    override fun toString(): String {
        return "PreLobbyManager(countdown=$countdown)"
    }


}