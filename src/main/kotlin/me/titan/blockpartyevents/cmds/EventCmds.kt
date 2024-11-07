package me.titan.blockpartyevents.cmds

import me.titan.blockpartyevents.cmds.event.EventJoinCmd
import me.titan.blockpartyevents.model.event.EventType
import me.titan.blockpartyevents.util.StringMapping
import me.titan.titanlib.commands.MainCommand
val acceptedEvents = StringMapping<EventType>()
    .add("oitc", "oneinthechamber", "one_in_the_chamber").to(EventType.OITC)
    .add("fil", "floorislava", "floor_is_lava").to(EventType.FIL)
    .add("sg","survivalgames", "survivalgame","survival_games", "survival_game").to(EventType.SG)
    .add("spleef").to(EventType.SPLEEF)
class EventCmds: MainCommand("event") {

    init {
        registerSubCommand(EventJoinCmd(this))
    }


}