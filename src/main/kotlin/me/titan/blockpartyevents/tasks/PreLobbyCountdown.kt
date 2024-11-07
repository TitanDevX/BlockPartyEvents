package me.titan.blockpartyevents.tasks

import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.plugin
import me.titan.titanlib.common.Common
import me.titan.titanlib.common.TimeUtil
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

open class PreLobbyCountdown(val event: BREvent): BukkitRunnable() {
    var seconds = 0
    var cooldown = 0
    init {
        runTaskTimer(plugin(), 0, 20)
        cooldown = (event.getConfig().basicSettings.preLobbyCountdown/1000).toInt()
    }

    override fun run() {

        val rem = seconds
        if(rem%60 == 0 && rem >= 60){
        broadcast()
        }
        if(rem == 30 || rem == 5 || seconds == 0){
            broadcast()
        }

        println(
            "CD $cooldown $rem"
        )
        if(rem >= cooldown){
            finish()
            cancel()
        }
        seconds++
    }
    open fun finish(){

    }
    private fun broadcast(){

        Messages.EVENT_JOIN_BROADCAST.messagesOrMessage?.let {

            val e = it.map {
                it.replace("%event%", event.getConfig().humanName)
                .replace("%starting_in%", TimeUtil.formatTimeGeneric(
                (cooldown-seconds).toLong()
            )) }
            for (p in Bukkit.getOnlinePlayers()) {
                for (s in e) {
                    val cb = ComponentBuilder(Common.colorize(s))
                    cb.event(ClickEvent(ClickEvent.Action.RUN_COMMAND,"/event join"))
                    p.sendMessage(*cb.create())
                }
            }


        }

    }

}