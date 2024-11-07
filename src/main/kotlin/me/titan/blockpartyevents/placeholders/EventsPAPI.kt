package me.titan.blockpartyevents.placeholders

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.titan.blockpartyevents.cache.PlayerCache
import me.titan.blockpartyevents.cmds.acceptedEvents
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

class EventsPAPI: PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "bpevents"
    }

    override fun getAuthor(): String {
       return "TitanDev"
    }

    override fun getVersion(): String {
        return "1"
    }

    override fun onPlaceholderRequest(player: Player, params: String): String {

        val args = params.split("_")
        if(args.size == 2) {
            if (args[0].equals("wins")) {
                val type = acceptedEvents.map(args[1])
                if(type != null){
                    var pc = PlayerCache.players.getIfPresent(player.uniqueId)
                    if(pc == null){
                      pc = PlayerCache.getOrLoadPlayerCache(player.uniqueId).call()
                    }
                    if(pc == null) return "0"
                    return pc.wins.getOrDefault(type,0).toString()

                }

            }
        }
        return "invalid placeholder"
    }
}