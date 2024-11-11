package me.titan.blockpartyevents.db

import me.titan.blockpartyevents.cache.PlayerCache
import me.titan.blockpartyevents.mainConfig
import me.titan.blockpartyevents.model.event.EventType
import me.titan.blockpartyevents.runAsync
import me.titan.blockpartyevents.util.DbCallable
import me.titan.blockpartyevents.util.createDbCallable
import me.titan.titanlib.common.LocationUtil
import org.bukkit.Location
import java.util.UUID
import java.util.concurrent.CompletableFuture

class PlayersDb(val dbManager: DatabaseManager) {

    var table = mainConfig().getMysqlPlayersTable()
    init {
        initTable()
    }

    private fun initTable(){


            dbManager.getConnection().use {
                it.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS $table(" +
                            "uuid VARCHAR(36)," +
                            "wins_fil INT DEFAULT 0," +
                            "wins_sg INT DEFAULT 0," +
                            "wins_spleef INT DEFAULT 0," +
                            "wins_oitc INT DEFAULT 0," +
                            "last_loc VARCHAR(100)" +
                            "PRIMARY KEY(uuid))"
                ).use {
                    it.executeUpdate()

                }
            }

    }
    fun reload(){
        initTable()
    }

    fun loadPlayerCache(uuid: UUID): DbCallable<PlayerCache> {

        return createDbCallable{
                            dbManager.getConnection().use {
                it.prepareStatement(
                    "SELECT * FROM $table" +
                            " WHERE uuid=?"
                ).use {
                    it.setString(1, uuid.toString())
                    it.executeQuery().use {
                        val pc = PlayerCache(uuid)
                        if (it.next()) {
                            pc.wins[EventType.FIL] = it.getInt("wins_fil")
                            pc.wins[EventType.SG] = it.getInt("wins_sg")
                            pc.wins[EventType.OITC] = it.getInt("wins_oitc")
                            pc.wins[EventType.SPLEEF] = it.getInt("wins_spleef")
                            it.getString("last_loc")?.let {
                                pc.lastLoc = LocationUtil.LocfromString(it)
                            }
                        }
                       return@createDbCallable pc
                    }
                }
            }
        }


    }

    fun updatePlayerCacheWins(uuid: UUID, event: EventType, newWins: Int): CompletableFuture<Void>  {

        val f = CompletableFuture<Void>();
        val fieldName = "wins_${event.name.lowercase()}";
        {
            dbManager.getConnection().use {
                it.prepareStatement(
                    "INSERT INTO $table(uuid, $fieldName) VALUES(?, ?) ON DUPLICATE KEY UPDATE " +
                            "$fieldName=VALUES($fieldName)").use {
                    it.setString(1, uuid.toString())
                    it.setInt(2,newWins)
                    it.executeUpdate()
                    f.complete(null)
                }
            }
        }.runAsync().onFailure {
            f.completeExceptionally(it.cause)
        }
        return f;

    }
    fun updatePlayerCacheWins(uuid: UUID, event: EventType): CompletableFuture<Void>  {

        val f = CompletableFuture<Void>();
        val fieldName = "wins_${event.name.lowercase()}";
        {
            dbManager.getConnection().use {
                it.prepareStatement(
                    "INSERT INTO $table(uuid, $fieldName) VALUES(?, 1) ON DUPLICATE KEY UPDATE " +
                            "$fieldName=$fieldName + 1").use {
                    it.setString(1, uuid.toString())
                    it.executeUpdate()
                    f.complete(null)
                }
            }
        }.runAsync().onFailure {
            f.completeExceptionally(it.cause)
        }
        return f;

    }
    fun updatePlayerCacheLastLoc(uuid: UUID, location: String?): CompletableFuture<Void>  {

        val f = CompletableFuture<Void>();
        {
            dbManager.getConnection().use {
                it.prepareStatement(
                    "INSERT INTO $table(uuid, last_loc) VALUES(?, ?) ON DUPLICATE KEY UPDATE " +
                            "last_loc=VALUES(last_loc)").use {
                    it.setString(1, uuid.toString())
                    it.setString(2,location)
                    it.executeUpdate()
                    f.complete(null)
                }
            }
        }.runAsync().onFailure {
            f.completeExceptionally(it.cause)
        }
        return f;

    }
}
