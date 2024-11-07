package me.titan.blockpartyevents.api.event

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 *
 *
 * @param canceledFinishStages There are 3 stages of event finishing: give_rewards and notify, update_db, if you want to cancel any of them put it in this list.
 */
class EventPreEndEvent(val event: me.titan.blockpartyevents.model.event.BREvent, winner: Player,
                       val canceledFinishStages: MutableList<String> = mutableListOf()): Event() {
    private var cancelled = false
    override fun getHandlers(): HandlerList {
        return getHandlerList()
    }
    companion object{
        private val handlerList: HandlerList = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList{
            return handlerList
        }
    }


}
