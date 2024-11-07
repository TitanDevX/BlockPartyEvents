package me.titan.blockpartyevents.model.event

import me.titan.blockpartyevents.api.event.EventCancelCause
import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.config.model.EventConfig
import me.titan.blockpartyevents.eventManager
import me.titan.blockpartyevents.games.Game
import me.titan.blockpartyevents.manager.phase.*
import me.titan.blockpartyevents.plugin
import me.titan.blockpartyevents.permissionPrefix
import org.bukkit.entity.Player
import java.util.*
import me.titan.blockpartyevents.util.tell
import me.titan.titanlib.common.Common
import org.bukkit.Bukkit
import org.bukkit.event.Event
import java.util.function.Consumer
import java.util.function.Function
import kotlin.collections.ArrayList

class BREvent(val type: EventType, val info: EventInfo ) {


    var state: EventState = EventState.IDLE
    // every player who ever joined the game
    val players = ArrayList<UUID>()
    val onlinePlayers = ArrayList<Player>()
    val spectators = ArrayList<Player>()

    private val plPhase: PreLobbyManager = PreLobbyManager(this)
    private val loPhase: LobbyPhaseManager = LobbyPhaseManager(this)
    private val gmPhase: GamePhaseManager = GamePhaseManager(this)
    private val fhPhase: FinishedPhaseManager = FinishedPhaseManager(this)
    var currentPhase: EventPhaseManager? = null
    fun broadcast(m: Messages, func: Function<String, String>? = null){
        val str = m.messagesOrMessage?.map{func?.apply(it)}
        ForAllOnlinePlayers { p->
            str?.forEach { msg ->
                Common.tell(p,msg)
            }
        }
    }
    fun ForAllOnlinePlayers(con: Consumer<Player>){
        for (player in onlinePlayers) {
            con.accept(player)
        }
        for (player in spectators) {
            con.accept(player)
        }
    }
    fun joinPlayer(player: Player): Boolean{

        if(onlinePlayers.contains(player)){
            Messages.JOIN_FAIL_ALREADY_JOINED.tell(player)
            return false
        }
        if(currentPhase == null){
            Messages.JOIN_FAIL_ERROR.tell(player)
            return false
        };
        if(!currentPhase!!.joinPlayer(player)) return false
        if(!players.contains(player.uniqueId))
            players.add(player.uniqueId)
        onlinePlayers.add(player)
        broadcast(Messages.JOIN_BROADCAST) {
            it.replace("%player%",player.name)
                .replace("%players%", onlinePlayers.size.toString())
                .replace("%max%",getConfig().basicSettings.playersAmount.max.toString())
        }

        return true
    }
    fun leavePlayer(player: Player): Boolean{
        if(!onlinePlayers.contains(player) && !spectators.contains(player)){
            Messages.LEAVE_FAIL_NOT_JOINED.tell(player)
            return false
        }
        if(currentPhase == null){return false};
        if(!currentPhase!!.allowLeavePlayer(player)) return false
        onlinePlayers.remove(player)
        spectators.remove(player)
        Messages.YOU_LEFT_EVENT.tell(player)
        plugin().playerManager.resetPlayer(player)
        currentPhase!!.leavePlayer(player)
        return true
    }

    @Suppress
    fun getNextPhaseManager(): EventPhaseManager {
        if(state == EventState.PRE_LOBBY){
            return plPhase
        }else if(state == EventState.LOBBY){
            return loPhase
        }else if(state == EventState.STARTED){
            return gmPhase
        }else if(state == EventState.FINISHED){
            return fhPhase
        }
        throw IllegalArgumentException("There is no phase for idle state.")

    }

    fun getGamePhaseManager(): GamePhaseManager {
        return gmPhase
    }

    /**
     *
     *
     */
    @Deprecated("Use EventManager#cancel instead.")
    fun cancel(cancelCause: EventCancelCause): Boolean{
        if(currentPhase != null){
            currentPhase!!.cancel(cancelCause)
        }
        state = EventState.IDLE

        if(cancelCause == EventCancelCause.ERROR){
            for(p in Bukkit.getOnlinePlayers()){
                if(p.hasPermission("$permissionPrefix.admin")){
                    p.tell("&cThe event was cancelled due to an error, please check console for more details [this message is only sent to players with permission $permissionPrefix.admin]")
                }
            }
            ForAllOnlinePlayers {
                Messages.EVENT_CANCELLED_ERROR.tell(it, "%event%", getConfig().humanName)
            }
        }else if(cancelCause == EventCancelCause.COMMAND){
            ForAllOnlinePlayers {
                Messages.EVENT_CANCELLED_BY_ADMIN.tell(it, "%event%", getConfig().humanName)
            }
        }else if(cancelCause == EventCancelCause.NO_ENOUGH_PLAYERS){
            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                Messages.EVENT_CANCELLED_NO_ENOUGH_PLAYERS.tell(onlinePlayer,
                    "%event%", getConfig().humanName)

            }
        }
        ForAllOnlinePlayers {
            plugin().playerManager.resetPlayer(it)

        }
        return true
    }
    fun validateSpawns(): Boolean{
        try {
            val globalSpawnsUnSet = info.spawns.validateGlobal()
            val gameSpawnsUnSet = info.spawns.validate(type, getConfig())
            val unsetSpawns = globalSpawnsUnSet + gameSpawnsUnSet

            if (!unsetSpawns.isEmpty()) {
                plugin().logger.severe("Event was cancelled, please set the following spawns:");
                plugin().logger.severe("Global spawns not set: ${globalSpawnsUnSet.joinToString { it }}");
                plugin().logger.severe("${type.name.lowercase()} spawns not set: ${gameSpawnsUnSet.joinToString { it }}");
                eventManager().cancelEvent(EventCancelCause.ERROR)
            }
            return true;
        }catch (t: Throwable){
            t.printStackTrace()
            return false
        }
    }

    /**
     * @return true - must cancel the event, false otherwise
     *
     */
    fun onEvent(pe: Event){
        if(currentPhase == null) return
        currentPhase!!.onEvent(pe)
    }
    fun validateConfig(): Boolean{
         val b=  gmPhase.game!!.validateConfig()
        if(!b){
            plugin().logger.severe("Event was cancelled, some config fields need to be set.");
            eventManager().cancelEvent(EventCancelCause.ERROR)
        }
        return b
    }
    fun getCountdownRemaining(): String? {
        return currentPhase?.getRemainingCountdown()
    }

    fun getConfig(): EventConfig {
        return type.getConfig()
    }

    fun getGame(): Game {
        return gmPhase.game!!
    }

    override fun toString(): String {
        return "BREvent(type=$type, info=$info, state=$state, players=$players, onlinePlayers=$onlinePlayers, spectators=$spectators, plPhase=$plPhase, loPhase=$loPhase, gmPhase=$gmPhase, currentPhase=$currentPhase)"
    }


}