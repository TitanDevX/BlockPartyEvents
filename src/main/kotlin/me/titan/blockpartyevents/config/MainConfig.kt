package me.titan.blockpartyevents.config

import me.titan.blockpartyevents.config.model.EventBasicSettings
import me.titan.blockpartyevents.config.model.Reward
import me.titan.blockpartyevents.config.model.types.FILEventConfig
import me.titan.blockpartyevents.config.model.types.OITCEventConfig
import me.titan.blockpartyevents.config.model.types.SGEventConfig
import me.titan.blockpartyevents.config.model.types.SpleefEventConfig
import me.titan.blockpartyevents.plugin
import me.titan.blockpartyevents.util.Two
import me.titan.titanlib.common.NumberRange
import me.titan.titanlib.config.ConfigOption
import me.titan.titanlib.config.ConfigValue
import me.titan.titanlib.config.SimpleConfig
import me.titan.titanlib.config.element.ConfigTitle
import java.util.concurrent.ThreadLocalRandom

class MainConfig: SimpleConfig("config.yml", plugin()) {

    @ConfigOption(path = "event_start_countdown_title")
     var eventStartCountdownTitle: ConfigTitle? = null

    @ConfigOption(path = "event_start_title")
    var eventStartTitle: ConfigTitle? = null

    @ConfigOption(path = "death_match_title")
    var deathMatchTitle: ConfigTitle? = null
    @ConfigOption(path = "death_match_countdown_title")
    var deathMatchCountdownTitle: ConfigTitle? = null
    @ConfigOption(path = "event_finish_players_spawn_delay", parseTime = true)
    var eventFinishPlayersSpawnDelay: Long? = null


    var universalSettings: EventBasicSettings;
    var FILEventConfig: FILEventConfig;
   var OITCEventConfig: OITCEventConfig;
   var SGEventConfig: SGEventConfig;
    var SpleefEventConfig: SpleefEventConfig;

    @ConfigOption(path = "rewards.amount_range")
    val rewardGiveRange: NumberRange? = null
    val rewards = ArrayList<Reward>()
    init {
        val now = System.currentTimeMillis()
        plugin().logger.info("Loading main config...")

        loadReflection(this)
        universalSettings = EventBasicSettings.load(this, "universal_basic_settings")
        FILEventConfig = FILEventConfig(this)
        OITCEventConfig = OITCEventConfig(this)
        SGEventConfig = SGEventConfig(this)
        SpleefEventConfig = SpleefEventConfig(this)

        plugin().logger.info("Loading FIL config...")
        loadReflection(FILEventConfig,FILEventConfig::class.java)
        plugin().logger.info("Loaded FIL config.")

        plugin().logger.info("Loading OTIC config...")
        loadReflection(OITCEventConfig,OITCEventConfig::class.java)
        plugin().logger.info("Loaded OTIC config.")

        plugin().logger.info("Loading SG config...")
        loadReflection(SGEventConfig,SGEventConfig::class.java)
        plugin().logger.info("Loaded SG config.")

        plugin().logger.info("Loading Spleef config...")
        loadReflection(SpleefEventConfig, SpleefEventConfig::class.java)
        plugin().logger.info("Loaded Spleef config.")

        config.getMapList("rewards.list").let {
            for (item in it) {
                val cmd = item["cmd"];
                val text = item["text"];
                if(cmd == null || text == null){
                    plugin().logger.warning("Invalid reward in config, you must specify text and cmd for each reward to work.")
                    continue
                }
                rewards.add(Reward(cmd.toString(),text.toString()))
            }
            plugin().logger.info("Loading ${rewards.size} rewards.")
        }

        plugin().logger.info("Finished loading main config in ${System.currentTimeMillis()-now}ms")
    }

    fun getMysqlHost(): String {
        return config.getString("mysql.host")!!
    }

    fun getMysqlPort(): Int {
        return config.getInt("mysql.port")
    }

    fun getMysqlDb(): String {
        return config.getString("mysql.database")!!
    }

    fun getMysqlUser(): String {
        return config.getString("mysql.user")!!
    }

    fun getMysqlPassword(): String {
        return config.getString("mysql.password")!!
    }

    fun getMysqlPlayersTable(): String {
        return config.getString("mysql.players_table")!!
    }
    fun getMysqlSpawnsTable(): String {
        return config.getString("mysql.spawns_table")!!
    }
    fun isChatRouteEnabled(): Boolean {
        return config.getBoolean("route_chat")
    }
    fun chatFormatSettings(): Two<Boolean, String?> {
        return Two(config.getBoolean("format_chat.enabled"),
            config.getString("format_chat.format"));
    }
    fun getRandomRewards(): List<Reward>{
        val list = ArrayList<Reward>()
        if(rewardGiveRange == null) return list
        val n = rewardGiveRange.random


        outerLoop@for(i in 1..Math.min(n,rewards.size)){
            var randReward: Reward? = null
            var tries = 0
            while(randReward == null || list.contains(randReward)){
                randReward = rewards.get(ThreadLocalRandom.current().nextInt(0,rewards.size));
                tries++
                if(tries >= rewards.size){
                    break
                }
            }
            if(randReward != null)
                list.add(randReward)
        }

        return list
    }

}