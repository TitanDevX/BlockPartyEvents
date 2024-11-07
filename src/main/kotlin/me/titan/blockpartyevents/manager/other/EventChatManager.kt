package me.titan.blockpartyevents.manager.other

import me.titan.blockpartyevents.eventManager
import me.titan.blockpartyevents.mainConfig
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.playerManager
import org.bukkit.event.player.AsyncPlayerChatEvent

class EventChatManager {


    companion object {
        fun onChat(e: AsyncPlayerChatEvent) {
            val event: BREvent = eventManager().currentEvent ?: return

            val formatSettings = mainConfig().chatFormatSettings()
            if (!mainConfig().isChatRouteEnabled()) return
            val userEvent = playerManager().getEvent(e.player) != null
            if (!userEvent) {
                e.recipients.removeIf { event.onlinePlayers.contains(it) || event.spectators.contains(it) }
                return
            }

            event!!.ForAllOnlinePlayers {
                if (formatSettings.obj1) {
                    e.format = formatSettings.obj2!!
                        .replace("%player_display_name%", "%s")
                        .replace("%message%", "%s")
                }

                e.recipients.removeIf { !event.onlinePlayers.contains(it) && !event.spectators.contains(it) }

            }

        }
    }

}