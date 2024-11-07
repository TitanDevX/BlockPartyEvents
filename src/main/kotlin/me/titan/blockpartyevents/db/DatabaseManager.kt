package me.titan.blockpartyevents.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.titan.blockpartyevents.plugin
import java.sql.Connection

class DatabaseManager {

    lateinit var dataSource: HikariDataSource;

    var established = false;
    init {
        plugin().logger.info("Connecting to the database...")
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl =
            "jdbc:mysql://${plugin().mainConfig.getMysqlHost()}:${plugin().mainConfig.getMysqlPort()}/${plugin().mainConfig.getMysqlDb()}?characterEncoding=latin1"

        hikariConfig.driverClassName = "com.mysql.jdbc.Driver"
        hikariConfig.username = plugin().mainConfig.getMysqlUser()
        hikariConfig.password = plugin().mainConfig.getMysqlPassword()

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true")
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250")
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        dataSource = HikariDataSource(hikariConfig)

        try{
            getConnection().close();
            established = true;
            plugin().logger.info("Successfully connected to the database.")
        }catch (e: Throwable){
            plugin().logger.severe("Unable to connect to database: ${e.message}")
            established = false;
        }

    }
    fun reload(){
        close()
        plugin().logger.info("Reloading connection to the database...")
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl =
            "jdbc:mysql://${plugin().mainConfig.getMysqlHost()}:${plugin().mainConfig.getMysqlPort()}/${plugin().mainConfig.getMysqlDb()}?characterEncoding=latin1"

        hikariConfig.driverClassName = "com.mysql.jdbc.Driver"
        hikariConfig.username = plugin().mainConfig.getMysqlUser()
        hikariConfig.password = plugin().mainConfig.getMysqlPassword()
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true")
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250")
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        dataSource = HikariDataSource(hikariConfig)
        try{
            getConnection().close();
            established = true;
            plugin().logger.info("Successfully connected to the database.")
        }catch (e: Throwable){
            plugin().logger.severe("Unable to connect to database: ${e.message}")
            established = false;
        }
    }
//    private fun initTable(){
//
//        val q = "CREATE TABLE IF NOT EXISTS $table (" +
//                "uuid VARCHAR(36) NOT NULL," +
//                "level INT," +
//                "exp INT," +
//                "double_exp_expiration BIGINT," +
//                "PRIMARY KEY (uuid));"
//        getConnection().use { con ->
//            con.prepareStatement(q).use {
//                it.executeUpdate();
//            }
//        }
//
//    }
    fun getConnection(): Connection {
        return dataSource.connection;
    }
    fun close(){
        dataSource.close()
    }
    companion object {
        @JvmStatic
        fun reloadAll(){
            plugin().spawnsDb.dbManager.reload()
            plugin().spawnsDb.reload()
            plugin().playersDb.reload()
        }
    }
}