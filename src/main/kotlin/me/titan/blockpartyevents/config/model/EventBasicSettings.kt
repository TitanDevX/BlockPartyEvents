package me.titan.blockpartyevents.config.model

import me.titan.blockpartyevents.mainConfig
import me.titan.blockpartyevents.util.ConfigCountdown
import me.titan.titanlib.common.NumberRange
import me.titan.titanlib.common.TimeUtil
import me.titan.titanlib.config.ConfigHolder
import me.titan.titanlib.config.ConfigOption
import me.titan.titanlib.config.ConfigValue
import me.titan.titanlib.config.SimpleConfig
import org.bukkit.configuration.ConfigurationSection

@ConfigValue(loadMethodRawConfigAndPath = "load")
class EventBasicSettings() {


     var isUniversal: Boolean = false;
    var playersAmount: NumberRange = NumberRange(0,0)
        get() {
            if(isUniversal) return mainConfig().universalSettings.playersAmount
            return field
        }
    var preLobbyCountdown: Long = 0
        get() {
            if(isUniversal) return mainConfig().universalSettings.preLobbyCountdown
            return field
        }
    var lobbyCountdown: ConfigCountdown = ConfigCountdown(null)
        get() {
            if(isUniversal) return mainConfig().universalSettings.lobbyCountdown
            return field
        }

    companion object{

        @JvmStatic
       fun load(sc: SimpleConfig, path: String): EventBasicSettings {
            val v = EventBasicSettings()
            if(!sc.config.isConfigurationSection(path)){
                v.isUniversal = true;

                return v;
            }
            v.playersAmount = NumberRange.fromString(sc.config.getString("$path.players_amount"))
            v.preLobbyCountdown = TimeUtil.parseToken(sc.config.getString("$path.pre_lobby_countdown"))
            v.lobbyCountdown = ConfigCountdown(sc.config.getString("$path.lobby_countdown"))

            return v
        }
    }

}