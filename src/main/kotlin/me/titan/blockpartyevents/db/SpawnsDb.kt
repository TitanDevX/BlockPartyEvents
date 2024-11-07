package me.titan.blockpartyevents.db

import me.titan.blockpartyevents.mainConfig
import me.titan.blockpartyevents.model.event.EventSpawns
import me.titan.blockpartyevents.model.event.EventType
import me.titan.blockpartyevents.runAsync
import me.titan.titanlib.common.LocationUtil
import org.bukkit.Location
import java.util.concurrent.CompletableFuture

class SpawnsDb( val dbManager: DatabaseManager) {

    init {
        initTable()
    }

    private fun initTable(){

        val table = mainConfig().getMysqlSpawnsTable();

            dbManager.getConnection().use {
                it.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS $table(" +
                            "id VARCHAR(20)," +
                            "eventType VARCHAR(6)," +
                            "location VARCHAR(100)," +
                            "PRIMARY KEY(id))"
                ).use {
                    it.executeUpdate()

                }
            }

    }
    fun reload(){
        initTable()
    }
    fun getLocation(id: String, eventType: EventType ): CompletableFuture<Location?> {

        val f = CompletableFuture<Location?>();
        {
            dbManager.getConnection().use {
                it.prepareStatement(
                    "SELECT location FROM ${mainConfig().getMysqlSpawnsTable()}" +
                            " WHERE id=? AND eventType=?"
                ).use {
                    it.setString(1, id)
                    it.setString(2, eventType.name)
                    it.executeQuery().use {
                        if (it.next()) {
                            val str = it.getString("location")
                            f.complete(LocationUtil.LocfromString(str))
                        }else{
                            f.complete(null)
                        }
                    }
                }
            }
        }.runAsync().onFailure {
            f.completeExceptionally(it.cause)
        }
        return f;

    }
    fun loadAll(eventType: EventType, spawns: EventSpawns, async: Boolean = true): CompletableFuture<Any> {
        val f = CompletableFuture<Any>();
        {
            dbManager.getConnection().use {
                it.prepareStatement(
                    "SELECT id, location, eventType FROM ${mainConfig().getMysqlSpawnsTable()}" +
                            " WHERE eventType=? OR eventType IS NULL"
                ).use {
                    it.setString(1, eventType.name)
                    it.executeQuery().use {
                        while (it.next()) {
                            val eventType = it.getString("eventType")
                            var id = it.getString("id")
                            val str = it.getString("location")

                            spawns.put(id,LocationUtil.LocfromString(str))
                        }
                        f.complete(spawns)
                    }
                }
            }
        }.runAsync(async).onFailure {
            f.completeExceptionally(it.cause)
        }
        return f;
    }
    fun putLocation(id: String, eventType: String?, loc: Location): CompletableFuture<Any> {

        val f = CompletableFuture<Any>();
        {
            dbManager.getConnection().use {
                it.prepareStatement(
                    "INSERT INTO ${mainConfig().getMysqlSpawnsTable()}" +
                            " VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE id=VALUES(id), eventType=VALUES(eventType), location=VALUES(location)"
                ).use {
                    it.setString(1, id)
                    it.setString(2, eventType)
                    it.setString(3,LocationUtil.formatBlockLoc(loc,6))
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
