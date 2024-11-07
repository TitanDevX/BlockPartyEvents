package me.titan.blockpartyevents.api.event

import me.titan.blockpartyevents.config.model.Reward
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EventPostEndEvent(val event: me.titan.blockpartyevents.model.event.BREvent, winner: Player, rewards: List<Reward>?): Event() {
    override fun getHandlers(): HandlerList {
        return getHandlerList()
    }

    companion object {
        private val handlerList: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }

}
