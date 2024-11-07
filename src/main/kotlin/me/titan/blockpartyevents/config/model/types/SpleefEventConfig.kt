package me.titan.blockpartyevents.config.model.types

import me.titan.blockpartyevents.config.model.EventConfig
import me.titan.blockpartyevents.model.event.EventType
import me.titan.titanlib.config.ConfigItem
import me.titan.titanlib.config.ConfigOption
import me.titan.titanlib.config.SimpleConfig
import org.bukkit.Material

class SpleefEventConfig(sc: SimpleConfig) : EventConfig(sc) {



    var loseBlock: Material? = null


    @ConfigOption(path = "shovel_item")
    var shovelItem: ConfigItem? = null
    @ConfigOption(path = "shovel_shoot_cooldown")
    var shovelShootCooldown: Double? = null
    var envBlocks: ArrayList<Material> = ArrayList<Material>()
    init {

        sc.config.getConfigurationSection("Events.SPLEEF")?.let {
            for (key in it.getKeys(false)) {
            }
        }
        println(sc.config.get(pathPrefix() + "environment_blocks")) //environment_blocks
        sc.config.getStringList(pathPrefix() + "environment_blocks").let {
            for (key in it) {
                Material.getMaterial(key.uppercase())?.let { it1 -> envBlocks.add(it1) }
            }
        }
         sc.config.getString(pathPrefix() + "lose_block")?.let {
             loseBlock = Material.getMaterial(it.uppercase())
         }
    }
    override fun pathPrefix(): String {
        return "Events.${EventType.SPLEEF.name}."
    }




}