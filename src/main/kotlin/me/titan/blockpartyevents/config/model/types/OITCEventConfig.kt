package me.titan.blockpartyevents.config.model.types

import me.titan.blockpartyevents.config.model.EventConfig
import me.titan.blockpartyevents.model.event.EventType
import me.titan.titanlib.config.ConfigItem
import me.titan.titanlib.config.ConfigOption
import me.titan.titanlib.config.SimpleConfig

class OITCEventConfig(sc: SimpleConfig) : EventConfig(sc) {



    @ConfigOption(path = "lives")
    var lives: Int? = null

    @ConfigOption(path = "items.sword")
    var swordItem: ConfigItem? = null


    @ConfigOption(path = "items.bow")
    var bowItem: ConfigItem? = null

    @ConfigOption(path = "items.arrow")
    var arrowItem: ConfigItem? = null

    override fun pathPrefix(): String {
        return "Events.${EventType.OITC.name}."
    }


}