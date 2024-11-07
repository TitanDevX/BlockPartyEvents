package me.titan.blockpartyevents.manager.phase

import me.titan.blockpartyevents.api.event.EventCancelCause
import me.titan.blockpartyevents.config.messages.Messages
import me.titan.blockpartyevents.games.Game
import me.titan.blockpartyevents.model.event.BREvent
import me.titan.blockpartyevents.model.event.EventState
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.block.BlockPlaceEvent

class GamePhaseManager(event: BREvent): EventPhaseManager(event) {
     var game: Game? = null
         get() {
             if(field == null){
                 field = Game.createGame(event, this);
             }
             return field
         }


    val originalBlocks = HashMap<Location, Material>()

    override fun startState(): EventState {
        return EventState.STARTED
    }

    override fun finishState(): EventState {

        return EventState.FINISHED
    }

    override fun check(vararg params: Any): Boolean {
        return true
    }

    override fun joinPlayer(player: Player): Boolean {
        Messages.JOIN_FAIL_ALREADY_STARTED.tell(player)
        return false
    }

    override fun leavePlayer(player: Player) {
        game!!.onPlayerLeave(player)
    }

    override fun onEvent(e: Event) {
        super.onEvent(e)
        if(e is BlockBreakEvent){
            val ev = e as BlockEvent
            originalBlocks[ev.block.location] =ev.block.type
        }else  if(e is BlockPlaceEvent) {
            val ev = e as BlockEvent
            originalBlocks[ev.block.location] = Material.AIR
        }
         game!!.onEvent(e)
    }
    override fun startNoChecks(vararg params: Any) {

        game!!.start()
    }
    fun initGame(){

        game!!.init()
    }

    /**
     * @param final following value:
     *
     * 1 - clears block placed during the event (typically used after the event finish and teleporting player to spawn).
     *
     * 3 - trigger game's game finish handle. (typically used as soon as the event finishes)
     *
     * 2 - does both of above. (typically used when cancelling event during game phase)
     */
    fun onGameFinish(final: Int) {
        if (final <= 2) {
            for (entry in originalBlocks.entries) {
                entry.key.block.type = entry.value
            }
            originalBlocks.clear()
        }
        if(final >= 2){
            game!!.interrupt()
        }
    }

    override fun cancel(cause: EventCancelCause) {
        onGameFinish(2)
    }
    fun SetBlock(loc: Location, mat: Material) {

        originalBlocks[loc] = loc.block.type
        loc.block.type = mat

    }

    override fun toString(): String {
        return "GamePhaseManager(game=$game, originalBlocks=$originalBlocks)"
    }


}