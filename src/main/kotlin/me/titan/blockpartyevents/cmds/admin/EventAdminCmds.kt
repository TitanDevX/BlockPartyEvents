package me.titan.blockpartyevents.cmds.admin

import me.titan.blockpartyevents.cmds.admin.sub.*
import me.titan.titanlib.commands.MainCommand

class EventAdminCmds: MainCommand("eventadmin") {

    init {
        registerSubCommand(AdminSetSpawnCmd(this))
        registerSubCommand(AdminReloadCmd(this))
        registerSubCommand(AdminEventStateCmd(this))
        registerSubCommand(AdminEventCancelCmd(this))
        registerSubCommand(AdminEventStartCmd(this))
    }

}