package me.titan.blockpartyevents.util

import me.titan.titanlib.common.TimeUtil
import me.titan.titanlib.config.ConfigValue
import java.util.concurrent.TimeUnit

@ConfigValue(loadMethodString = "fromString")
class ConfigCountdown(str: String? = null) {

    var time: Long = 0;
    var period: Long = 0;
    constructor(time: Long, period: Long) : this() {
        this.period = period
        this.time = time;
    }
    init {

        str?.let {
            it.split("/").let {
                time = TimeUtil.parseToken(it[0])
                period = TimeUtil.parseToken(it[1])
            }
        }

    }
    fun toSecondTimeUnit(): ConfigCountdown{
        val time = TimeUnit.SECONDS.convert(time,TimeUnit.MILLISECONDS);
        val period = TimeUnit.SECONDS.convert(period,TimeUnit.MILLISECONDS);
        return ConfigCountdown(time,period)
    }


    companion object{
        @JvmStatic
        fun fromString(str: String): ConfigCountdown {
            return ConfigCountdown(str)
        }
    }


}