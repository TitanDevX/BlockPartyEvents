package me.titan.blockpartyevents.config.model

import me.titan.titanlib.config.ConfigHolder
import me.titan.titanlib.config.ConfigOption
import me.titan.titanlib.config.SimpleConfig

abstract class EventConfig(val sc: SimpleConfig): ConfigHolder {


    @ConfigOption(path = "basic_settings")
    lateinit var basicSettings: EventBasicSettings

    @ConfigOption(path = "human_name")
    lateinit var humanName: String

}