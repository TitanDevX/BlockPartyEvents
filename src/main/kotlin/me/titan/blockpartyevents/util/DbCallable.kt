package me.titan.blockpartyevents.util

import me.titan.blockpartyevents.runAsync
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture

fun <T> createDbCallable(func: () -> T ): DbCallable<T>{
    return object : DbCallable<T>(){
        override fun run(): T {
           return func()
        }
    }
}

/**
 * A util class that allows you to execute predefined functions either async or sync
 *
 * This is meant to be easy to create & use.
 */
abstract class DbCallable<T>: Callable<T> {


    private val ac = ArrayList<( T?, Throwable?) -> Unit>()
    protected abstract fun run(): T;
    override fun call(): T? {
        var thh: Throwable? = null
        var v: T? = null
        try{
            v = run()
        }catch (th: Throwable){
            thh = th
        }

        for (function in ac) {
            function(v,thh)
        }
        return v
    }
    fun callAsync(): CompletableFuture<T>{
        val f = CompletableFuture<T>();
        {
            f.complete(call())
        }.runAsync().onFailure {
            f.completeExceptionally(it.cause)
        }
        return f

    }
    fun thenAccept(func: (T?, Throwable?) -> Unit): DbCallable<T>{

        ac.add(func)
        return this
    }

    companion object {
        @JvmStatic
        fun <T> createDbCallable(func: () -> T ): DbCallable<T>{
            return object : DbCallable<T>(){
                override fun run(): T {
                    return func()
                }
            }
        }
        fun <T> computedDbCallable(value: T): DbCallable<T> {
            return object : DbCallable<T>(){
                override fun run(): T {
                    return value
                }
            }
        }
    }

}