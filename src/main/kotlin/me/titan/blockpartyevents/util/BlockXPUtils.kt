package me.titan.blockpartyevents.util

import me.titan.blockpartyevents.config.messages.Messages
import me.titan.titanlib.common.Common
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

fun willItemGoTo(e: InventoryClickEvent,range: IntRange): Boolean {

    if (e.isShiftClick) {
        val list = if(e.clickedInventory == e.view.topInventory) e.view.bottomInventory.contents
        else e.view.topInventory.contents;
        var airSlot = -1
        var stacked = e.currentItem!!.amount
        var t: ItemStack?

        for (p in list.indices) {
            t = list[p]
            if (t == null) {
                if (airSlot == -1) {
                    airSlot = p
                }
                continue
            }
            if (t.type != e.currentItem!!.type) {
                continue
            } else if (t.durability != e.currentItem!!.durability) {
                continue
            } else if (t.maxStackSize == t.amount) {
                continue
            } else {
                if (t.amount + stacked > t.maxStackSize) {
                    stacked -= t.maxStackSize - t.amount
                    if(p in range) return true;
                } else {
                    stacked = 0
                    if(p in range) return true;
                    break
                }
            }
        }
        if (airSlot != -1 && stacked > 0) {
            if(airSlot in range) return true;
        }
    }
    return false;

}
/**
 * Get item destination slots when shift + clicked
 */
fun getShiftSlots(e: InventoryClickEvent): List<Int> {
    val slots: MutableList<Int> = ArrayList()
    if (e.isShiftClick) {
        val list = if(e.clickedInventory == e.view.topInventory) e.view.bottomInventory.contents
        else e.view.topInventory.contents;
        var airSlot = -1
        var stacked = e.currentItem!!.amount
        var t: ItemStack?

        for (p in list.indices) {
            t = list[p]
            if (t == null) {
                if (airSlot == -1) {
                    airSlot = p
                }
                continue
            }
            if (t.type != e.currentItem!!.type) {
                continue
            } else if (t.durability != e.currentItem!!.durability) {
                continue
            } else if (t.maxStackSize == t.amount) {
                continue
            } else {
                if (t.amount + stacked > t.maxStackSize) {
                    stacked -= t.maxStackSize - t.amount
                    slots.add(p)
                } else {
                    stacked = 0
                    slots.add(p)
                    break
                }
            }
        }
        if (airSlot != -1 && stacked > 0) {
            slots.add(airSlot)
        }
    }
    return slots
}
fun CallEvent(event: Event){
    Bukkit.getPluginManager().callEvent(event)
}
fun <T> CallEvent(event: T): Boolean
    where T : Event,
            T : Cancellable{
   Bukkit.getPluginManager().callEvent(event)
   return event.isCancelled
}
fun isItemEmpty(item: ItemStack?): Boolean{
    return item == null || item.isEmpty;
}
fun CommandSender.tell (msg: String, prefixed: Boolean = true){
    Common.tell(this,(if(prefixed) Messages.prefix + " " else "") + msg)
}
operator fun <T> ArrayList<T>.plus(o: ArrayList<T>): ArrayList<T>{
    val l = ArrayList<T>()
    l.addAll(this)
    l.addAll(o)
    return l
}