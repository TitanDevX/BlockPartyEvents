package me.titan.blockpartyevents.model.event

import me.titan.blockpartyevents.plugin
import java.util.concurrent.CompletableFuture

/**
 * starter: the player who started the event's name or 'console'.
 */
class EventInfo(val starter: String,val type: EventType) {

    val spawns: EventSpawns = EventSpawns()


    init {



    }


    fun load(): CompletableFuture<Any> {
        return plugin().spawnsDb.loadAll(type, spawns)
    }

    override fun toString(): String {
        return "EventInfo(starter='$starter', type=$type, spawns=$spawns)"
    }


}