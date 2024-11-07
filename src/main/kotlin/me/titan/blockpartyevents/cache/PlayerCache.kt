package me.titan.blockpartyevents.cache

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import me.titan.blockpartyevents.model.event.EventType
import me.titan.blockpartyevents.plugin
import me.titan.blockpartyevents.util.DbCallable
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


class PlayerCache(val uuid: UUID) {
    val wins = HashMap<EventType, Int>()

    fun getAllWins(): Int {
        return wins.values.sum()
    }


    companion object {

        @JvmStatic
        val players: Cache<UUID, PlayerCache> = CacheBuilder.newBuilder()
            .expireAfterAccess(1,TimeUnit.MINUTES).build<UUID,PlayerCache>()

        @JvmStatic
        /**
        * Gets loaded PlayerCache in memory otherwise returns an empty PlayerCache.
        */
        fun getPlayerCache(uuid: UUID): PlayerCache {
            return players.get(uuid) {PlayerCache(uuid)};
        }

        /**
         * Gets loaded PlayerCache in memory or loads it (this may return an empty PlayerCache)
         */
        @JvmStatic
        fun getOrLoadPlayerCache(uuid: UUID): DbCallable<PlayerCache> {
            val pc = players.getIfPresent(uuid)
            if(pc != null){
                return DbCallable.computedDbCallable(pc)
            }
                val f = plugin().playersDb.loadPlayerCache(uuid)
                f.thenAccept { pc, th ->
                    if(pc == null) return@thenAccept
                    cache(uuid, pc)
                }
                return f

        }

        /**
         * Caches PlayerCache instance
         */
        @JvmStatic
        fun cache(uuid: UUID, playerCache: PlayerCache){
            players.put(uuid,playerCache)
        }

        /**
         * Updates player's event wins.
         *
         * ###### You can call this method safely without checking for player existence in the db
         */
        @JvmStatic
        fun updateWins(uuid: UUID, event: EventType, newWins: Int): CompletableFuture<Void> {
           val f = plugin().playersDb.updatePlayerCacheWins(uuid,event,newWins)
            // Update if there is a stored cache instance.
            f.thenAccept {
                 players.getIfPresent(uuid)?.let {
                     it.wins[event] = newWins
                 }
            }
            return f
        }
        /**
         * Updates player's event wins (Increases it by 1).
         *
         * ###### You can call this method safely without checking for player existence in the db
         */
        @JvmStatic
        fun updateWins(uuid: UUID, event: EventType): CompletableFuture<Void> {
            val f = plugin().playersDb.updatePlayerCacheWins(uuid,event)
            // Update if there is a stored cache instance.
            f.whenComplete { pc, err ->

                if(err  != null){
                    plugin().logger.severe("Failed to load player's cache (uuid: ${uuid}):")
                err.printStackTrace()

                return@whenComplete
                }
                players.getIfPresent(uuid)?.let {
                    it.wins[event] = it.wins.getOrDefault(event,0)+1
                }
            }
            return f
        }
    }

}