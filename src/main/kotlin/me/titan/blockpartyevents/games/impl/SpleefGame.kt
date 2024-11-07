package me.titan.blockpartyevents.games.impl

import de.tr7zw.changeme.nbtapi.NBT
import me.titan.blockpartyevents.config.model.types.SpleefEventConfig
import me.titan.blockpartyevents.games.Game
import me.titan.blockpartyevents.mainConfig
import me.titan.blockpartyevents.manager.phase.GamePhaseManager
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.model.event.EventState
import me.titan.blockpartyevents.plugin
import me.titan.titanlib.common.MetaManager
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class SpleefGame(event: BREvent, manager: GamePhaseManager): Game(event, manager, 1) {


    val playerData = HashMap<String ,SpleefPlayerData>()




    override fun init() {
    useNoDamage()

    }
    override fun start() {
        super.start()
        for (p in event.onlinePlayers) {
            getConfig().shovelItem!!.let {
                p.inventory.setItem(it.slot,setItemMeta(it.item,"shovel"))
            }
            playerData[p.name] = SpleefPlayerData()
        }
    }

    override fun tick() {
        if(event.state != EventState.STARTED) return
        val eliminate = ArrayList<Player>()

        for (p in event.onlinePlayers) {
            p.location.clone().subtract(0.0,1.0,0.0).let { loc ->
            //    checkFaces.forEach {
                    if(loc.block.type == getConfig().loseBlock!!){
                        eliminate.add(p)

                        return@let
                       // return@forEach
                  //  }
                }

            }
        }
        for (player in eliminate) {
            eliminatePlayer(player)
        }
    }



    override fun validateConfig(): Boolean {

        val b = arrayOf(
            getConfig().envBlocks.isNotEmpty(),
            getConfig().loseBlock != null,
            getConfig().shovelItem != null,
            getConfig().shovelShootCooldown != null)
        if(b.all { it }) return true
        for(i in b.indices){
            if(i == 0){
                plugin().logger.severe("Please set environment blocks for SPLEEF event in config. the event will be cancelled.")

            }else if(i == 1){
                plugin().logger.severe("Please set lose block for SPLEEF event in config. the event will be cancelled.")

            }else if(i == 2){
                plugin().logger.severe("Please set shovel item for SPLEEF event in config. the event will be cancelled.")

            }else if(i == 3){
                plugin().logger.severe("Please set shovel shoot cooldown for SPLEEF event in config. the event will be cancelled.")

            }
        }
        return false
    }
    fun getConfig(): SpleefEventConfig {
        return mainConfig().SpleefEventConfig;
    }


    override fun handleEvent(e: Event){
        if(e is EntityDamageEvent){

                e.damage = 0.0

        }else if(e is PlayerInteractEvent){

            if(e.item == null) return;
            if(isEventItem(e.item!!,"shovel")){
                val pd = playerData[e.player.name]
                if(pd == null) return;
                val dif = System.currentTimeMillis()-pd.lastShoot
                if(pd.lastShoot != 0L && dif < getConfig().shovelShootCooldown!!.times(1000).toLong()){
                    return;
                }
                shoot(e.player)
                pd.lastShoot = System.currentTimeMillis()

            }

        }else if(e is ProjectileHitEvent){
            if(e.entity.hasMetadata(SNOWBALL_META) ){
                if(e.hitBlock != null){
                    if(!getConfig().envBlocks.contains(e.hitBlock!!.type) && getConfig().loseBlock != e.hitBlock!!.type){
                        SetBlock(e.hitBlock!!.location,Material.AIR)
                    }

                }else{
                    e.isCancelled = true
                }

            }
        }
    }



    private val NBTId = "SPLEEFGAMEITEM"
    private val SNOWBALL_META = "SPLEEFEVENTSNOWBALL";
    private fun shoot(player: Player){
        player.launchProjectile(Snowball::class.java, null) {
            it.isGlowing = true
            it.fireTicks = 20
            MetaManager.setMetadataObject(it,SNOWBALL_META,player)
        }
    }
    private fun isEventItem(item: ItemStack, expectedString: String): Boolean{
        return NBT.get<Boolean>(item) {
            it.getString(NBTId).equals(expectedString)
        }
    }
    private fun setItemMeta(item: ItemStack, id: String): ItemStack {

        NBT.modify(item) {
            it.setString(NBTId,id)
        }
        return item
    }

    override fun eliminatePlayer(player: Player, makeSpectator: Boolean) {
        playerData.remove(player.name)
        super.eliminatePlayer(player, makeSpectator)
    }
    override fun toString(): String {
        return "SpleefGame(playerData=$playerData, NBTId='$NBTId', SNOWBALL_META='$SNOWBALL_META')"
    }

    companion object{
        @JvmStatic
        private val checkFaces = BlockFace.entries.filter { !it.name.contains("_") }.toList()
    }
}
class SpleefPlayerData() {
    var lastShoot: Long = 0

}