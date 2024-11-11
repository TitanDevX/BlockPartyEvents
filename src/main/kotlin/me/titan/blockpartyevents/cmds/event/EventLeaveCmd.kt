package me.titan.blockpartyevents.cmds.event

import me.titan.blockpartyevents.cmds.EventCmds
import me.titan.blockpartyevents.cmds.acceptedEvents
import me.titan.blockpartyevents.eventManager
import me.titan.blockpartyevents.manager.PlayerManager
import me.titan.blockpartyevents.model.event.EventType
import me.titan.blockpartyevents.permissionPrefix
import me.titan.blockpartyevents.playerManager
import me.titan.blockpartyevents.util.StringMapping
import me.titan.blockpartyevents.util.tell
import me.titan.titanlib.commands.SubCommand
import me.titan.titanlib.common.Common
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class EventLeaveCmd(cmd: EventCmds): SubCommand("leave"
    , "","$permissionPrefix.leave",1,false,cmd) {
    override fun execute(s: CommandSender, args: Array<out String>) {

        val p = s as Player
        if(!p.hasMetadata(PlayerManager.PLAYER_EVENT_METADATA)){
            playerManager().handlePlayerLeaveNotInEvent(p)
            return
        }
        eventManager().leaveEvent(p)

    }
}