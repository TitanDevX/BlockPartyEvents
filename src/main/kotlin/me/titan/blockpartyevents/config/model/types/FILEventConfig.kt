package me.titan.blockpartyevents.config.model.types

import me.titan.blockpartyevents.config.model.EventConfig
import me.titan.blockpartyevents.model.event.EventType
import me.titan.titanlib.config.ConfigOption
import me.titan.titanlib.config.SimpleConfig

class FILEventConfig(sc: SimpleConfig) : EventConfig(sc) {

    @ConfigOption(path = "lava_rise.interval", parseTime = true)
    var lavaRiseInterval: Long? = null

    @ConfigOption(path = "lava_rise.amount")
    var lavaRiseAmount: Int? = null

    override fun pathPrefix(): String {
        return "Events.${EventType.FIL.name}."
    }


}