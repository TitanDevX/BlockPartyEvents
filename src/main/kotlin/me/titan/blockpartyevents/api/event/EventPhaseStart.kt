package me.titan.blockpartyevents.api.event

import me.titan.blockpartyevents.manager.phase.EventPhaseManager
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EventPhaseStart(val event: me.titan.blockpartyevents.model.event.BREvent, phaseManager: EventPhaseManager): Event(), Cancellable {
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

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(p0: Boolean) {
        cancelled = p0
    }
}