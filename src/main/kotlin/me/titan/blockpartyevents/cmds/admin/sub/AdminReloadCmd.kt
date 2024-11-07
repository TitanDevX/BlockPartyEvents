package me.titan.blockpartyevents.cmds.admin.sub

import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.permissionPrefix
import me.titan.blockpartyevents.plugin
import me.titan.titanlib.commands.MainCommand
import me.titan.titanlib.commands.SubCommand
import me.titan.titanlib.common.Common
import org.bukkit.command.CommandSender

class AdminReloadCmd(cmd: MainCommand):
    SubCommand("reload","","$permissionPrefix.admin.setspawn",
        0,true,cmd) {
    override fun execute(s: CommandSender, args: Array<out String>) {

        Common.tell(s,"${Messages.prefix}&aReloading plugin...")
       kotlin.runCatching {
        plugin().reload()
       }.onSuccess {
           Common.tell(s,"${Messages.prefix}&aReloaded the plugin successfully.")
       }.onFailure {
           it.printStackTrace()
           Common.tell(s,"${Messages.prefix}&cError while reloading the plugin, check console..")
       }
    }


}