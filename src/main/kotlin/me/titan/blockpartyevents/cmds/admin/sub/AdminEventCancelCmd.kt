package me.titan.blockpartyevents.cmds.admin.sub

import me.titan.blockpartyevents.api.event.EventCancelCause
import me.titan.blockpartyevents.cmds.acceptedEvents
import me.titan.blockpartyevents.db.SpawnsDb
import me.titan.blockpartyevents.eventManager
import me.titan.blockpartyevents.model.event.EventSpawns
import me.titan.blockpartyevents.permissionPrefix
import me.titan.blockpartyevents.plugin
import me.titan.titanlib.commands.MainCommand
import me.titan.titanlib.commands.SubCommand
import me.titan.titanlib.common.Common
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AdminEventCancelCmd(cmd: MainCommand):
    SubCommand("cancelevent","","$permissionPrefix.admin.cancelevent",0,true,cmd) {
    override fun execute(s: CommandSender, args: Array<out String>) {

        try {

            eventManager().cancelEvent(EventCancelCause.COMMAND)

        }catch (t: Throwable){
            t.printStackTrace()
        }

    }


}