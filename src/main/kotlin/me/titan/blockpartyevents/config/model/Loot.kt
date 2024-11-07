package me.titan.blockpartyevents.config.model

import me.titan.blockpartyevents.plugin
import me.titan.titanlib.common.NumberRange
import org.bukkit.Material

class Loot(val material: Material, val rarity: Int, val amount: NumberRange) {

    var probability = 0.0
    companion object {
        @JvmStatic
        fun fromString(key: String, value: String?): Loot?{
            if(value == null) return null
            value.split("/").let {
                val r = it[0].toInt()
                var range: NumberRange? = null;
                if(it.size == 2){
                    range = NumberRange.fromString(it[1])
                }else{
                    range = NumberRange(1,1)
                }
                val mat = Material.getMaterial(key.uppercase())
                if(mat == null){
                    plugin().logger.warning("Invalid material in sg-loot.yml: $key")
                    return null
                }
                return Loot(mat,r,range!!)

            }
        }
    }

}