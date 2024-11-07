package me.titan.blockpartyevents.games.impl

import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.config.model.Loot
import me.titan.blockpartyevents.config.model.types.SGEventConfig
import me.titan.blockpartyevents.games.Game
import me.titan.blockpartyevents.mainConfig
import me.titan.blockpartyevents.manager.phase.GamePhaseManager
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.plugin
import me.titan.blockpartyevents.util.AliasMethod
import me.titan.titanlib.common.Common
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SGGame(event: BREvent, manager: GamePhaseManager): Game(event, manager) {



    private lateinit var methodProp: HashMap<Int, Loot>
    private lateinit var method: AliasMethod
    /** 0 = didnt start
     1 = first cooldown in progress
     2 = second cooldown
    3 = started **/
    private var deathMatchState = 0
    private var countdownStartSecond = 0
    private var currentCountdown = 0L
    init {
        useDeathSystem()
        useDropsAllowed()
    }
    override fun init() {

        val config = plugin().sgLootConfig
        methodProp = HashMap(config.loots.size)
        val arr = ArrayList<Double>(config.loots.size)
        var i = 0
        for (loot in config.loots.values) {
            methodProp[i] = loot
            arr.add(i++,loot.probability )
        }
        method = AliasMethod(arr)
        var placed = 0
        for (en in event.info.spawns.entries) {

            if(en.key.contains("chest")){
                val loc = en.value
                if(loc.block.type == Material.CHEST){
                    ((loc.block.state as Chest)).blockInventory.clear()
                }else{
                    SetBlock(loc,Material.CHEST);
                }

                val chest = loc.block.state as Chest
                populateChest(chest)
                placed++
            }
        }
        plugin().logger.info("Placed $placed chests for survivalgames event." )

    }
    private fun populateChest(chest: Chest){
        val itemsAm = mainConfig().SGEventConfig.lootItemsAmount!!.random
        for(i in 1..itemsAm){

           val lootId = method.next()
            val loot = methodProp[lootId]
            if(loot == null){
                plugin().logger.severe("Error while populating chest: alias method returned invalid alias. ($lootId)")
                continue
            }

            chest.blockInventory.addItem(ItemStack(loot.material))
        }
        //chest.update()
    }

    override fun start() {
        super.start()
    }

    override fun tick() {

        if(deathMatchState == 2){
            val startsIn = (seconds-countdownStartSecond)-currentCountdown

            if(startsIn <= 5){
                event.ForAllOnlinePlayers { p ->
                    mainConfig().deathMatchCountdownTitle?.applyPlaceholder {
                        it.replace(
                            "%seconds%",
                            startsIn.toString()
                        )
                    }?.let {
                        p.sendTitle(Common.colorize(it.title), Common.colorize(it.subTitle))
                    }
                }
            }else{
                event.broadcast(Messages.DEATH_MATCH_STARTING_IN) {it.replace("%starts_in%",startsIn.toString())}

            }
        }
        val dif = seconds - countdownStartSecond
        if (countdownStartSecond != -1 && dif > currentCountdown) {
            countdownStartSecond = -1
            updateDeathMatchState()

        }

    }

    override fun handleEvent(e: Event) {
    }

    override fun eliminatePlayer(player: Player, makeSpectator: Boolean) {
        if(event.onlinePlayers.size <= getConfig().deathMatchTrigger!!
            && deathMatchState == 0){
            deathMatchState = 1
            updateDeathMatchState()
        }

        super.eliminatePlayer(player,makeSpectator)

    }


    private fun updateDeathMatchState(){
        if(deathMatchState == 1){
            event.broadcast(Messages.DEATH_MATCH_ALERT)
            deathMatchState = 2;
            countdownStartSecond = seconds
            currentCountdown = getConfig().deathMatchFirstCd!!
        }else if(deathMatchState== 2){
            deathMatchState = 3
            for(i in 1..event.onlinePlayers.size){
                val p = event.onlinePlayers.get(i-1)
                val loc = event.info.spawns.get("deathmatch_$i")
                if(loc == null) {
                    plugin().logger.severe("&cError has happened.")
                    continue
                }
                p.teleport(loc)
                p.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS,-1,9999))

            }
            countdownStartSecond = seconds
            currentCountdown = getConfig().deathMatchSecondCd!!


        }else if(deathMatchState == 3){
            deathMatchState = 4
            event.ForAllOnlinePlayers { p->
                p.removePotionEffect(PotionEffectType.SLOWNESS)
                Messages.DEATH_MATCH_STARTED.tell(p)
                mainConfig().deathMatchTitle?.let {
                    p.sendTitle(Common.colorize(it.title),Common.colorize(it.subTitle))
                }

            }
        }

    }




    override fun validateConfig(): Boolean {

        val b = arrayOf(getConfig().deathMatchTrigger != null,
            getConfig().lootItemsAmount != null,
            getConfig().deathMatchFirstCd != null,
            getConfig().deathMatchSecondCd != null)
        if(b.all { it }) return true
        for(i in b.indices){
            if(i == 0){
                plugin().logger.severe("Please set death match trigger for survival games in config. the event will be cancelled.")

            }else if(i == 1){
                plugin().logger.severe("Please set loot items amount for survival games in config. the event will be cancelled.")

            }else if(i == 2){
                plugin().logger.severe("Please set death match first cd for survival games in config. the event will be cancelled.")

            }else if(i == 3){
                plugin().logger.severe("Please set death match second cd for survival games in config. the event will be cancelled.")
            }
        }
        return false
    }
    fun getConfig(): SGEventConfig {
        return mainConfig().SGEventConfig
    }

    override fun toString(): String {
        return "SGGame(methodProp=$methodProp, method=$method, deathMatchState=$deathMatchState, countdownStartSecond=$countdownStartSecond, currentCountdown=$currentCountdown)"
    }

}