package me.titan.blockpartyevents.games.impl

import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.config.model.types.FILEventConfig
import me.titan.blockpartyevents.games.Game
import me.titan.blockpartyevents.mainConfig
import me.titan.blockpartyevents.manager.phase.GamePhaseManager
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.plugin
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageEvent
import kotlin.math.max
import kotlin.math.min

class FILGame(event: BREvent, manager: GamePhaseManager): Game(event, manager) {

    var lastLavaRaise = 0
    var initalLavaY = 0
    var currentLavaY = 0;
    var todoUppers = 0
    lateinit var point1: Location
    lateinit var point2: Location

    override fun init() {
        useNoDamage()
        val loc1 = event.info.spawns["lava_point1"]!!.clone()
        val loc2 = event.info.spawns["lava_point2"]!!.clone()
        if(loc1.blockY == loc2.blockY){
            currentLavaY = loc2.blockY
        }else{
            // lets assume that point2 is always the greater just until we change point1 y.
            if(loc1.y > loc2.y){
                point2 =  loc1
                point1 = loc2
            } else{
                point2 = loc2
                point1 = loc1
            }
            currentLavaY = point2.blockY
            point1.y = point2.y
        }
        initalLavaY = currentLavaY
    }
    override fun start() {
        super.start()
    }

    override fun tick() {
        val dif = seconds-lastLavaRaise
        if(dif > getConfig().lavaRiseInterval!!/1000){
            upperLava(getConfig().lavaRiseAmount!!)
            lastLavaRaise = seconds
            event.ForAllOnlinePlayers {
                Messages.FIL_LAVA_RISE.tell(it)
            }
        }
        upperLava(0)
    }

    /**
     * So the concept of this function is:
     * it rises lava 1 block every second with the help of 'todoUppers'
     * every 'lavaRiseInterval' in config, it calls this method with the configured 'lavaRiseAmount' which typically would be greater than 1
     * so when this happens this method rises the lava 1 block and adds the remaining of 'lavaRiseAmount'
     * to 'todoUppers' var, so it can rises lava level in the next ticks, 1 block every second
     * , decreasing 'todoUppers' every rise.
     */
    private fun upperLava(by: Int){



        if(by > 1){
            todoUppers+=by-1
        }
        if(by == 0){
            if(todoUppers <= 0) return
            todoUppers--
        }
            point2.y++
            val maxX = max(point1.blockX,point2.blockX)
            val minX = min(point1.blockX,point2.blockX)
            val maxZ = max(point1.blockZ, point2.blockZ)
            val minZ = min(point1.blockZ, point2.blockZ)
            for(x in minX..maxX){
                for(y in point1.blockY..point2.blockY){
                    for(z in minZ..maxZ){
                        val loc = Location(point2.world,x.toDouble(),y.toDouble(),z.toDouble())
                        if(loc.block.type == Material.AIR)
                            SetBlock(loc,Material.LAVA) // only use our own function to set block material
                    }
                }
            }


    currentLavaY ++
    }


    override fun validateConfig(): Boolean {

        val b = arrayOf(getConfig().lavaRiseInterval != null, getConfig().lavaRiseAmount != null).all { it }
        if(b) return b
        if(getConfig().lavaRiseInterval == null){
            plugin().logger.severe("Please set lava rise interval in config. the event will be cancelled.")
        }
        if(getConfig().lavaRiseAmount == null){
            plugin().logger.severe("Please set lava rise amount in config. the event will be cancelled.")
        }
        return b
    }
    fun getConfig(): FILEventConfig {
        return mainConfig().FILEventConfig
    }


    override fun handleEvent(e: Event){
        if(e is EntityDamageEvent){
            if(e.cause == EntityDamageEvent.DamageCause.LAVA){
                event.ForAllOnlinePlayers {
                    Messages.PLAYER_DEATH_LAVA.tell(e.entity, "%victim%",e.entity.name)
                }
                eliminatePlayer(e.entity as Player)

            }
        }
    }

    override fun toString(): String {
        return "FILGame(lastLavaRaise=$lastLavaRaise, initalLavaY=$initalLavaY, currentLavaY=$currentLavaY, todoUppers=$todoUppers, point1=$point1, point2=$point2)"
    }


}