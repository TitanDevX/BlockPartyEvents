package me.titan.blockpartyevents.cmds.admin.sub

import me.titan.blockpartyevents.cmds.EventCmds
import me.titan.blockpartyevents.cmds.acceptedEvents
import me.titan.blockpartyevents.eventManager
import me.titan.blockpartyevents.model.event.EventType
import me.titan.blockpartyevents.permissionPrefix
import me.titan.blockpartyevents.util.tell
import me.titan.titanlib.commands.MainCommand
import me.titan.titanlib.commands.SubCommand
import me.titan.titanlib.common.Common
import org.bukkit.command.CommandSender



class AdminEventStartCmd(cmd: MainCommand): SubCommand("start", "<event type>","$permissionPrefix.admin.start",2,false,cmd) {
    override fun execute(s: CommandSender, args: Array<out String>) {
        val eventTypeStr = args[1]

        Common.tell(s,eventTypeStr)
        val type = acceptedEvents.map(eventTypeStr)
        if(type == null){
            s.tell("&cInvalid event type, accepted types: ${EventType.entries.joinToString { it.name }}")
            return
        }
        eventManager().startEvent(s,type)


    }
}