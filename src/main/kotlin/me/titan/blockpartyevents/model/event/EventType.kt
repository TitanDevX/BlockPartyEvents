package me.titan.blockpartyevents.model.event

import me.titan.blockpartyevents.config.model.EventConfig
import me.titan.blockpartyevents.mainConfig

enum class EventType {
    /**
     * ##### FLOOR IS LAVA
     * A minigame that kills players when they touch the lava.
     * When the event starts,players will spawn in a room with
     * obstacles and parkour that they can climb to achieve a
     * better position. PvP is enabled, so players can knock
     * each other into the lava. The lava will rise every 10-15
     * seconds and the last player to survive will win a prize!
      */
    FIL,

    /**
     * ##### One In The Chamber
     *
     * The classic one in the chamber gamemode. Players spawn with a bow ,
     * one arrow, and an iron sword. If they kill a player they get another arrow,
     * but if they get shot once, they die. If they miss their shot they will only
     * have an iron sword to fight with.
     */
    OITC,

    /**
     * ##### Survival Games
     * The original Survival Games. We will be using the original maps for this, so I want to try to re create that nostalgic feeling**
     *
     * - Deathmatch begins when only 8 players remain alive
     * - Chest spawn around the map with different loot each time. If you could add a way to configure this loot through vanilla IDs then randomize what loot is in what chest, that would be great.
     * - Fight to the death, the last player standing is the victor!
     */
    SG,

    /**
     * ##### Spleef
     * An event with multiple platforms on top of each other. The players compete to try to knock each other down to the next level while maintaining the highest position possible.
     *
     * - Shovels that shoot snowballs that instantly break the block it hits
     * - When a player falls off the final platform, they fall to their death
     */
    SPLEEF;
    fun getConfig(): EventConfig {
        if(this == SPLEEF){
            return mainConfig().SpleefEventConfig
        }else if(this == OITC){
            return mainConfig().OITCEventConfig
        }else if(this == SG){
            return mainConfig().SGEventConfig
        }else {
            return mainConfig().FILEventConfig
        }

    }
}