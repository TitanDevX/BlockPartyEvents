package me.titan.blockpartyevents

import me.clip.placeholderapi.PlaceholderAPI
import me.titan.blockpartyevents.cmds.EventCmds
import me.titan.blockpartyevents.cmds.admin.EventAdminCmds
import me.titan.blockpartyevents.config.MainConfig
import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.config.SGLootConfig
import me.titan.blockpartyevents.db.DatabaseManager
import me.titan.blockpartyevents.db.PlayersDb
import me.titan.blockpartyevents.db.SpawnsDb
import me.titan.blockpartyevents.listener.EventListener
import me.titan.blockpartyevents.listener.OutEventListener
import me.titan.blockpartyevents.manager.EventManager
import me.titan.blockpartyevents.manager.PlayerManager
import me.titan.blockpartyevents.placeholders.EventsPAPI
import me.titan.titanlib.TitanLib
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

val permissionPrefix = "blockevents"
private lateinit var plugin: EventsPlugin

fun plugin(): EventsPlugin {
    return plugin
}
fun mainConfig(): MainConfig {
    return plugin.mainConfig
}
fun eventManager(): EventManager {
    return plugin.eventManager
}
fun playerManager(): PlayerManager{
    return plugin.playerManager
}
fun <T> (() -> T).runAsync(): Result<Any>{
    return runCatching {

        Bukkit.getScheduler().runTaskAsynchronously(plugin()) { t ->
            this.invoke()
        }
    }
}
fun <T> (() -> T).runSync(): Result<Any>{
    return runCatching {
        Bukkit.getScheduler().runTask(plugin()) { t ->
            this.invoke()
        }
    }
}
fun <T> (() -> T).runAsync(isReallyAsync: Boolean): Result<Any>{
    return runCatching {

        (if(isReallyAsync) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin()) { t ->
                this.invoke()
            }
        }else{
            this.invoke()
        })!!
    }
}
class EventsPlugin: JavaPlugin() {

    lateinit var mainConfig: MainConfig;
    lateinit var sgLootConfig: SGLootConfig
    lateinit var spawnsDb: SpawnsDb
    lateinit var playersDb: PlayersDb
    lateinit var playerManager: PlayerManager
    lateinit var eventManager: EventManager

    override fun onEnable() {

        plugin = this;
        TitanLib.init(this)
        Messages.init(this,"messages.yml")
        mainConfig = MainConfig()
        sgLootConfig = SGLootConfig()
        val dbm = DatabaseManager()
        spawnsDb = SpawnsDb(dbm)
        playersDb = PlayersDb(dbm)

        eventManager = EventManager()
        playerManager = PlayerManager()

        Bukkit.getPluginManager().registerEvents(EventListener(),this)
        Bukkit.getPluginManager().registerEvents(OutEventListener(),this)

        EventCmds().register(this,"event")
        EventAdminCmds().register(this,"eventadmin")

        EventsPAPI().register()
        testDb()
    }

    fun testDb(){

        playersDb.loadPlayerCache(UUID.randomUUID()).call()


    }
    override fun onDisable() {

    }
    fun reload(){
        mainConfig = MainConfig()
        sgLootConfig = SGLootConfig()
        Messages.init(this,"messages.yml")
        DatabaseManager.reloadAll()
    }

    companion object {
        @JvmStatic
        fun getPlugin(): EventsPlugin {
            return plugin()
        }
    }

}