package me.titan.blockpartyevents.config.model

import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Reward(val cmd: String, val text: String) {

    fun grant(player: Player){
        try{
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd.replace("%player%",player.name))

        }catch (_:Throwable){

        }
    }

}