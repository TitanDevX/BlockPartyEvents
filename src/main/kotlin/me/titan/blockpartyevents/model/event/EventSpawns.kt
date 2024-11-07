package me.titan.blockpartyevents.model.event

import me.titan.blockpartyevents.config.model.EventConfig
import me.titan.blockpartyevents.mainConfig
import me.titan.titanlib.common.Common
import org.bukkit.Location
import org.bukkit.command.CommandSender

val globalSpawns = arrayOf("global_lobby")
val FILSpawns = arrayOf("lava_point1", "lava_point2", "spawn_x")
val SGSpawns = arrayOf("spawn_x", "chest_1", "deathmatch_x")
val OITCSpawns = arrayOf("spawn_x")
val SpleefSpawns = arrayOf("spawn_x")
class EventSpawns: HashMap<String, Location>() {


    /**
     * Validates global spawns are set
     *
     * @return list of global spawns that are not set.
     * an empty list indicates that all global spawns are set
     */
    fun validateGlobal(): List<String>{

      return validateList(globalSpawns, null)
    }

    fun validate(type: EventType, config: EventConfig): List<String>{
        if(type == EventType.FIL){
            return validateList(FILSpawns, config)
        }else if(type == EventType.SG){
            return validateList(SGSpawns, config)
        }else if(type == EventType.OITC){
            return validateList(OITCSpawns, config)
        }else {
            return validateList(SpleefSpawns, config)
        }
    }


    private fun validateList(l: Array<String>, config: EventConfig? = null): List<String>{
        val b = ArrayList<String>()
        var missingSpawnX = 0
        var missingDeathMatchX = 0
        for (spawn in l) {
            if(spawn.equals("spawn_x") && config != null){
                for (i in 1..config.basicSettings.playersAmount.min){
                    if(get("spawn_$i") == null){
                        missingSpawnX++
                    }
                }
                continue
            }else if(spawn.equals("deathmatch_x")){
                for (i in 1..mainConfig().SGEventConfig.deathMatchTrigger!!){
                    if(get("deathmatch_$i") == null){
                        missingDeathMatchX++
                    }
                }
                continue
            }
            if(get(spawn) == null) b.add(spawn)
        }
        if(missingSpawnX > 0){
            b.add("$missingSpawnX player spawns (spawn_<x>)")
        }
        if(missingDeathMatchX > 0){
            b.add("$missingDeathMatchX death match spawns (deathmatch_<x>)")
        }
        return b
    }


companion object{
    /**
     * @param eventType event type or null for global
     */
    @JvmStatic
    fun isSpawnNameValid(name:String, eventType: EventType?, s: CommandSender?): Boolean{
        if(eventType == EventType.OITC){
            return validateSpawnName(name, OITCSpawns,s)
        }else if(eventType == EventType.FIL){
            return validateSpawnName(name, FILSpawns,s)
        }else if(eventType == EventType.SG){
            return validateSpawnName(name, SGSpawns,s)
        }else if(eventType == EventType.SPLEEF){
            return validateSpawnName(name, SpleefSpawns,s)
        }else{
            return validateSpawnName(name, globalSpawns,s)
        }
    }
    @JvmStatic
    private fun validateSpawnName(name: String, l: Array<String>, s: CommandSender?): Boolean{
        for (str in l) {


            if(str.equals("spawn_x") && name.startsWith("spawn_") ){
                if(name.split("_")[1].toIntOrNull() == null){
                    s?.let {
                        Common.tell(s,"&cInvalid spawn name: a number is expected after 'spawn_'.")
                    }
                    continue
                }else{
                    return true
                }
            }else if(str.equals("chest_1")){
                if(name.split("_")[1].toIntOrNull() == null){
                    s?.let {
                        Common.tell(s,"&cInvalid spawn name: a number is expected after 'chest_'.")
                    }
                    continue
                }else{
                    return true
                }
            }else if(str.equals("deathmatch_x") && name.startsWith("deathmatch_")){
                if(name.split("_")[1].toIntOrNull() == null){
                    s?.let {
                        Common.tell(s,"&cInvalid spawn name: a number is expected after 'deathmatch_'.")
                    }
                    continue
                }else{
                    return true
                }
            }else if(str.equals(name, ignoreCase = true)) return true

        }
        return false
    }
}
}