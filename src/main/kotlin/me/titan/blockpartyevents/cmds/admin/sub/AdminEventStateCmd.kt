package me.titan.blockpartyevents.cmds.admin.sub

import me.titan.blockpartyevents.eventManager
import me.titan.blockpartyevents.permissionPrefix
import me.titan.blockpartyevents.util.tell
import me.titan.titanlib.commands.MainCommand
import me.titan.titanlib.commands.SubCommand
import me.titan.titanlib.common.Common
import org.bukkit.command.CommandSender

class AdminEventStateCmd(cmd: MainCommand):
    SubCommand("eventstate","","$permissionPrefix.admin.eventstate",0,true,cmd) {
    override fun execute(s: CommandSender, args: Array<out String>) {


        if(eventManager().currentEvent == null){
            s.tell("&cNo event is running.")
            return
        }

        eventManager().currentEvent!!.let {
            Common.tell(s,Common.fullChatLine("&6"),
                "&fEvent type&7: &b${it.getConfig().humanName}",
                "&fEvent state&7: &b${it.state}",
                "&fCountdown remaining&7: &b${it.getCountdownRemaining() ?: "none" }",
               "&fPlaying players&7: &b${it.onlinePlayers.size}",
                "&fSpectators&7: &b${it.spectators.size}",
                "&fAll joins&7: &b${it.players.size}",
                "Game: ${it.getGame()}",
                Common.fullChatLine("&6"))
        }






    }


}