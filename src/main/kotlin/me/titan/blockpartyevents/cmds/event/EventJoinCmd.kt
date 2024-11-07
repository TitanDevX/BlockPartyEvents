package me.titan.blockpartyevents.cmds.event

import me.titan.blockpartyevents.cmds.EventCmds
import me.titan.blockpartyevents.cmds.acceptedEvents
import me.titan.blockpartyevents.eventManager
import me.titan.blockpartyevents.model.event.EventType
import me.titan.blockpartyevents.permissionPrefix
import me.titan.blockpartyevents.util.StringMapping
import me.titan.blockpartyevents.util.tell
import me.titan.titanlib.commands.SubCommand
import me.titan.titanlib.common.Common
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class EventJoinCmd(cmd: EventCmds): SubCommand("join", "",
    "$permissionPrefix.join",1,false,cmd) {
    override fun execute(s: CommandSender, args: Array<out String>) {

        eventManager().joinEvent(s as Player)

    }
}