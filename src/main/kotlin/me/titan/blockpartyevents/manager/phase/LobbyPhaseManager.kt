package me.titan.blockpartyevents.manager.phase

import me.titan.blockpartyevents.api.event.EventCancelCause
import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.eventManager
import me.titan.blockpartyevents.mainConfig
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.tasks.EventStartCountdown
import me.titan.blockpartyevents.model.event.EventState
import me.titan.blockpartyevents.plugin
import me.titan.titanlib.common.Common
import me.titan.titanlib.common.TimeUtil
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class LobbyPhaseManager(event: BREvent): EventPhaseManager(event) {
    private var countdown: EventStartCountdown? = null
    val emptySpawns = ArrayList<Int>()
    override fun startState(): EventState {
        return EventState.LOBBY
    }
    var finished = false

    override fun finishState(): EventState {

        return EventState.STARTED
    }

    override fun check(vararg params: Any): Boolean {
        if(event.players.size < event.getConfig().basicSettings.playersAmount.min){
            eventManager().cancelEvent(EventCancelCause.NO_ENOUGH_PLAYERS)
            return false
        }
        return true
    }

    override fun joinPlayer(player: Player): Boolean {
        val max = event.getConfig().basicSettings.playersAmount.max
        if(event.onlinePlayers.size >= max){
            Messages.JOIN_FAIL_FULL.tell(player)
            return false;
        }
        if(finished){
            Messages.JOIN_FAIL_ALREADY_STARTED.tell(player)
            return false
        }
        val spawnI = if(emptySpawns.isEmpty()) event.onlinePlayers.size+1 else emptySpawns.randomOrNull()
        player.teleport(event.info.spawns["spawn_$spawnI"]!!)
        player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS,-1,100))
        player.inventory.clear()
        event.broadcast(Messages.LOBBY_LEAVE_BROADCAST) {
            it.replace("%player%",player.name)
                .replace("%players%", (event.onlinePlayers.size-1).toString())
                .replace("%max%",event.getConfig().basicSettings.playersAmount.max.toString())
        }
        return true
    }

    override fun leavePlayer(player: Player){
        player.removePotionEffect(PotionEffectType.SLOWNESS)
        val ind = event.onlinePlayers.indexOf(player)
        if(ind == -1) return
        emptySpawns.add(ind+1)

    }

    override fun onEvent(e: org.bukkit.event.Event) {
        super.onEvent(e)
        if(e is PlayerDeathEvent || e is BlockBreakEvent || e is BlockPlaceEvent) {
            (e as Cancellable).isCancelled = true
        }


    }

    override fun cancel(cause: EventCancelCause) {
        for (onlinePlayer in event.onlinePlayers) {
            onlinePlayer.removePotionEffect(PotionEffectType.SLOWNESS)
        }
        countdown?.cancel()
    }
    override fun getRemainingCountdown(): String? {
        return countdown?.let {
            TimeUtil.formatTimeShort(it.getRemainingTime()/1000)
        }
    }

    override fun startNoChecks(vararg params: Any) {
        for(i in 0..<event.onlinePlayers.size){
            val it = event.onlinePlayers.get(i)
            it.gameMode = GameMode.SURVIVAL
            it.teleport(event.info.spawns["spawn_${i+1}"]!!)
            it.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS,-1,100))
            it.inventory.clear()
            plugin().playerManager.setEventMetadata(it,event)
        }
        // lazy init the game map
        event.getGamePhaseManager().initGame()
        countdown = object : EventStartCountdown(event.getConfig().basicSettings.lobbyCountdown) {
            override fun broadcast(remainingTime: String) {
                event.ForAllOnlinePlayers {
                    Messages.LOBBY_COUNTDOWN.tell(it,"%rem_time%",remainingTime, "%event%",event.getConfig().humanName)
                }
            }

            override fun broadcastLast5Secs(sec: Int) {
                event.ForAllOnlinePlayers { p ->

                    mainConfig().eventStartCountdownTitle?.
                    applyPlaceholder { it.replace("%seconds%", sec.toString()).replace("%event%",event.getConfig().humanName) }?.let {

                            p.sendTitle(Common.colorize(it.title), Common.colorize(it.subTitle))
                        }

                }
            }
            override fun finish() {
                finished = true
                event.ForAllOnlinePlayers { p ->

                    p.gameMode = GameMode.SURVIVAL
                    p.removePotionEffect(PotionEffectType.SLOWNESS)
                    mainConfig().eventStartTitle?.
                    applyPlaceholder { it.replace("%event%",event.getConfig().humanName) }?.let {
                        p.sendTitle(Common.colorize(it.title), Common.colorize(it.subTitle))
                    }


                }
                startNext()
            }

        }
    }

    override fun toString(): String {
        return "LobbyPhaseManager(countdown=$countdown, emptySpawns=$emptySpawns, finished=$finished)"
    }


}