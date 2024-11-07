package me.titan.blockpartyevents.cmds.admin.sub

import me.titan.blockpartyevents.cmds.acceptedEvents
import me.titan.blockpartyevents.db.SpawnsDb
import me.titan.blockpartyevents.model.event.EventSpawns
import me.titan.blockpartyevents.permissionPrefix
import me.titan.blockpartyevents.plugin
import me.titan.titanlib.commands.MainCommand
import me.titan.titanlib.commands.SubCommand
import me.titan.titanlib.common.Common
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AdminSetSpawnCmd(cmd: MainCommand):
    SubCommand("setspawn","<name> <event type>","$permissionPrefix.admin.setspawn",3,true,cmd) {
    override fun execute(s: CommandSender, args: Array<out String>) {

        val p = s as Player
        val name = args[1]
        val eventTypeStr = args[2]
        val eventType = acceptedEvents.map(eventTypeStr)
        if(eventType == null && !eventTypeStr.contains("global")){
            Common.tell(s,"&cInvalid event type, did you mean 'global' ?")
            return;
        }
        if(!EventSpawns.isSpawnNameValid(name,eventType,s)){
            Common.tell(s,"&cInvalid spawn name.")
            return
        }
        Common.tell(s,"&aRegistering location $name for $eventTypeStr...")
        plugin().spawnsDb.putLocation(name, eventType?.name, p.location).whenComplete {ob, th ->
            if(th != null){
                Common.tell(s,"&cError while inserting spawn into database, please check console for more details, there might be a problem with your mysql configurations or a problem connecting to the database.")
                th.printStackTrace()

            }else{
                Common.tell(s,"&aSpawn location registered successfully!")
            }

        }

    }


}