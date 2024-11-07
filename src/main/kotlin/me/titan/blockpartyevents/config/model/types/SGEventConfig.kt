package me.titan.blockpartyevents.config.model.types

import me.titan.blockpartyevents.config.model.EventConfig
import me.titan.blockpartyevents.model.event.EventType
import me.titan.titanlib.common.NumberRange
import me.titan.titanlib.config.ConfigOption
import me.titan.titanlib.config.SimpleConfig

class SGEventConfig(sc: SimpleConfig) : EventConfig(sc) {


    @ConfigOption(path = "death_match_trigger")
    var deathMatchTrigger: Int? = null

    @ConfigOption(path = "loot_items_amount")
    var lootItemsAmount: NumberRange? = null

    @ConfigOption(path = "death_match_first_cd", parseTime = true)
    var deathMatchFirstCd: Long? = null

    @ConfigOption(path = "death_match_second_cd", parseTime = true)
    var deathMatchSecondCd: Long? = null
    override fun pathPrefix(): String {
        return "Events.${EventType.SG.name}."
    }


}