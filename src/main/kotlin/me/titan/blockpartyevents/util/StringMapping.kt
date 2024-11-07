package me.titan.blockpartyevents.util

class StringMapping<T: Enum<T>> {

    val v = HashMap<String, T>()

    val temp = ArrayList<String>()

    fun add(vararg e: String): StringMapping<T>{
        temp.addAll(e.map { it.lowercase() })
        return this
    }
    fun to(e: T): StringMapping<T>{
        for (s in temp) {
            v.put(s,e)
        }
        temp.clear()
        return this
    }
    fun map(str: String): T?{
        return v.get(str)
    }


}