package me.titan.blockpartyevents.tasks

import me.titan.blockpartyevents.plugin
import me.titan.blockpartyevents.util.ConfigCountdown
import me.titan.titanlib.common.TimeUtil
import org.bukkit.scheduler.BukkitRunnable

abstract class EventStartCountdown(var cc: ConfigCountdown) : BukkitRunnable(){

    val startTime = 0
    var currentTime = 0;
    var lastTick = 0
    init {
        cc = cc.toSecondTimeUnit()
        runTaskTimer(plugin(),0L, 20)

    }

    override fun run() {

        if(true){
            // TODO REMOVE
            cancel()
            finish()
        }

        val timeUntilFinish = cc.time-currentTime
        val timeUntilNextBd = cc.period-(currentTime-lastTick)
        if(timeUntilFinish > 5 && (currentTime == 0 || timeUntilNextBd <= 0L)){
            broadcast(TimeUtil.formatTimeShort(timeUntilFinish))
            lastTick = currentTime
        }else if(timeUntilFinish <= 5){
            broadcastLast5Secs(timeUntilFinish.toInt());
        }
        if(timeUntilFinish <= 0){
            cancel()
            finish()
        }
        currentTime++
    }

    abstract fun broadcast(remainingTime: String);
    open fun broadcastLast5Secs(sec: Int){
         broadcast("$sec seconds")
     }
    fun getRemainingTime(): Long{
        return (startTime + cc.time) - System.currentTimeMillis()
    }
    abstract fun finish()

}