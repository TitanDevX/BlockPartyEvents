package me.titan.blockpartyevents.manager.other

import me.titan.blockpartyevents.api.event.EventPostEndEvent
import me.titan.blockpartyevents.api.event.EventPreEndEvent
import me.titan.blockpartyevents.cache.PlayerCache
import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.config.model.Reward
import me.titan.blockpartyevents.mainConfig
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.plugin
import me.titan.titanlib.common.Common
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * Class handles everything about reward giving and notifying event finish and updating player's stats
 *
 * some may see thats its unnecessary to create a class for that,
 * but I'd like the project to be organized, portable, and scalable.
 */
class EventFinisher(val event: BREvent, val winner: Player) {

    var canceledStages: List<String>? = null
    var rewards: List<Reward>? = null

    fun start() {

        runCatching {
            canceledStages = callPreEvent()
        }.onFailure {
            handleFailure("calling api/EventPreEndEvent", it)
            return
        }


        if (!canceledStages!!.contains("give_rewards")) {
            kotlin.runCatching {
                 giveRewards()
            }.onFailure {
                handleFailure("giving rewards", it)
            }
        }
        if(!canceledStages!!.contains("notify")) {
            kotlin.runCatching {
                notify_()
            }.onFailure {
                handleFailure("send notifications", it)
            }
        }
        if(!canceledStages!!.contains("update_db")) {
            kotlin.runCatching {
                updatePlayerWins()
            }.onFailure {
                handleFailure("update stats in database", it)
            }
        }
        runCatching {
            callPostEvent()
        }.onFailure {
            handleFailure("calling api/EventPostEndEvent", it)
        }
    }

    private fun handleFailure(part: String, it: Throwable) {
        plugin().logger.severe(
            "Error while finishing event ${event.type}" +
                    " [error while $part]:"
        )
        it.printStackTrace()
    }

    private fun giveRewards() {

        val rewards = mainConfig().getRandomRewards()

        for (reward in rewards) {
            reward.grant(winner)
        }

        this.rewards = rewards

    }
    private fun notify_() {


        // broadcast
        var msgs = Messages.EVENT_FINISH_BROADCAST.messagesOrMessage
        var replMsgs = replaceMessage(msgs)
        for (player in Bukkit.getOnlinePlayers()) {
            Common.tell(player,replMsgs)
        }

        // winner
         msgs = Messages.EVENT_FINISH_WINNER.messagesOrMessage
        Common.tell(winner,replaceMessage(msgs))

        // all event players
        msgs = Messages.EVENT_FINISH_PLAYERS.messagesOrMessage
        replMsgs = replaceMessage(msgs)
        for (spectator in event.spectators) {
            Common.tell(spectator,replMsgs)
        }
        Common.tell(winner,replMsgs )

    }

    private fun updatePlayerWins(){

       // PlayerCache.getOrLoadPlayerCache(winner.uniqueId).whenComplete {pc, err ->
//            if(err != null){
//                plugin().logger.severe("Failed to load player's cache ${winner.name}:")
//                err.printStackTrace()
//
//                return@whenComplete
//            }

            PlayerCache.updateWins(winner.uniqueId,event.type)
        //}


    }
    private fun replaceMessage(msgs: List<String>?): List<String>? {
        if(msgs == null) return null
        val nlist = ArrayList<String>()
        for (msg in msgs) {
            if(msg.contains("%rewards%")){
                if(rewards == null) continue
                nlist.addAll(rewards!!.map { it.text })
            }else{
                nlist.add( msg.replace("%winner%", winner.displayName)
                    .replace("%event%", event.getConfig().humanName))
            }
        }

        return nlist
    }
    private fun callPreEvent(): List<String> {

        val ev = EventPreEndEvent(event, winner);
        Bukkit.getPluginManager().callEvent(ev)
        return ev.canceledFinishStages

    }

    private fun callPostEvent() {
        Bukkit.getPluginManager().callEvent(EventPostEndEvent(event, winner, rewards))
    }
}