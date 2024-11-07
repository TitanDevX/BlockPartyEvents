package me.titan.blockpartyevents.config

import me.titan.blockpartyevents.config.model.Loot
import me.titan.blockpartyevents.plugin
import me.titan.titanlib.config.SimpleConfig
import org.bukkit.Material

class SGLootConfig: SimpleConfig("sg-loot.yml", plugin()) {

    val loots = HashMap<Material, Loot>()

    init {
        plugin().logger.info("Loading survival games loot...")
        var sum = 0
        for (matName in config.getKeys(false)) {
           val loot = Loot.fromString(matName,config.getString(matName)) ?: continue
            loots[loot.material] = loot
//            if(loote.rarity > max){
//                max = loote.rarity
//            }
            sum+=loot.rarity
        }
        for (loot in loots.values) {
            // TODO maybe find a better way to calculate probability.
            loot.probability = (sum/loot.rarity.toDouble())/sum
        }
        plugin().logger.info("Loaded ${loots.size} survival games loot.")


    }

}