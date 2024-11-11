package me.titan.blockpartyevents.config.messages

import me.titan.titanlib.common.Common
import me.titan.titanlib.config.SimpleConfig
import me.titan.titanlib.messages.LibMessages
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.*
import java.util.function.Function

enum class Messages {
    PREFIX, NO_PERMISSION,
    TELEPORTING,
    LOBBY_COUNTDOWN,
    EVENT_CANCELLED_NO_ENOUGH_PLAYERS,
    EVENT_CANCELLED_BY_ADMIN,
    EVENT_CANCELLED_ERROR,
    EVENT_JOIN_BROADCAST,
    FIL_LAVA_RISE,
    JOIN_FAIL_ERROR,
    JOIN_FAIL_NO_EVENT,
    JOIN_FAIL_FULL,
    JOIN_FAIL_ALREADY_STARTED,
    JOIN_BROADCAST,
    LOBBY_LEAVE_BROADCAST,
    YOU_LEFT_EVENT,
    LEAVE_FAIL_NOT_JOINED,
    PLAYER_DEATH_LAVA,
    PLAYER_DEATH_KILL,
    LEAVE_BROADCAST,
    PLAYER_DEATH,
    DEATH_MATCH_ALERT,
    DEATH_MATCH_STARTING_IN,
    DEATH_MATCH_STARTED,
    OITC_PLAYER_RESPAWN,
    OITC_NO_LIVES,
    JOIN_FAIL_ALREADY_JOINED,
    EVENT_FINISH_BROADCAST,
    EVENT_FINISH_WINNER,
    EVENT_FINISH_PLAYERS,
    EVENT_FINISH_PLAYERS_AFTER_DELAY,
    LEAVE_FAIL_NO_EVENT,
    TELEPORTING_TO_SPAWN;

    var defaultMsg: String? = null

    constructor()
    constructor(defaultMsg: String) {
        this.defaultMsg = defaultMsg
    }

    // Controllers -------
//    private fun c(str: String): String {
//        var str = str
//        if (!str.contains("{center}")) {
//            return str
//        }
//        str = str.replace("{center}", "")
//        if (str.contains("{prefix}")) {
//            str = str.replace("{prefix}", "")
//            str = prefix(str)
//        }
//        return Common.getCenteredMessage(str)
//    }
//
//    private fun boxed(str: String): List<String> {
//        val list: MutableList<String> = ArrayList()
//        if (!str.contains("{boxed}")) {
//            list.add(str)
//            return list
//        }
//        list.add(Common.fullChatLine("&6"))
//        list.add(str.replace("{boxed}", ""))
//        list.add(Common.fullChatLine("&6"))
//        return list
//    }
//    private fun chatLine(str: String): String {
//        if (!str.contains("{chatline}")) {
//            return str;
//        }
//        return str.replace("{chatline}",Common.fullChatLine(""));
//    }
//
//    private fun all(str: String): String {
//        var str = str
//        str = prefix(str)
//        return c(str)
//    }
//
//    private fun prefix(str: String): String {
//        if (str.contains("{noprefix}")) {
//            return str.replace("{noprefix}", "")
//        }
//        return prefix + str
//    }

    @JvmOverloads
    fun tell(s: CommandSender?, replacer: Function<String, String>? = null) {
        val msg = plainObject ?: return
        if(!msg.parsed){
            msg.parse()
        }
        val msgs = messagesOrMessage ?: return
//        val shouldPrefix = msgs!!.size == 1
//        val nlist: MutableList<String> = ArrayList()
//        for (mm in msgs) {
//            var msg = mm;
//            if (replacer != null) msg = replacer.apply(msg)
//            msg = if (shouldPrefix) {
//                all(msg)
//            } else {
//                c(msg)
//            }
//            val sub = boxed(msg)
//            nlist.addAll(sub)
//        }
        msgs.map { replacer?.apply(it) ?: it }.forEach {
            Common.tell(s,it)
        }

    }

    fun tell(s: CommandSender?, replacer: String?, replacement: String?) {
        tell(s) { str: String -> str.replace(replacer!!, replacement!!) }
    }

    fun tell(s: CommandSender?, replacer1: String?, replacement1: String?, replacer2: String?, replacement2: String?) {
        tell(s) { str: String ->
            str.replace(replacer1!!, replacement1!!).replace(
                replacer2!!, replacement2!!
            )
        }
    }

    val message: String?
        get() {
            val obj = plainObject

            return obj?.content?.first()
        }
    val messages: MutableList<String>?
        get() {

            return plainObject?.content
        }
    val messagesOrMessage: List<String>?
        get() {
//            if (plainObject == null) return null
//            var msgs = this.messages
//            if (msgs == null) {
//                val msg = message
//                if (msg != null) {
//                    msgs = ArrayList()
//                    msgs.add(msg)
//                }
//            }
//            if (msgs == null) {
//                msgs = ArrayList()
//            }
//            return msgs
            return plainObject?.content
        }
    val plainObject: Message?
        get() = Companion.messages[this]


    companion object {
        private val messages: MutableMap<Messages, Message> = HashMap()

        val prefix: String?
            get() = PREFIX.message

        fun init(plugin: JavaPlugin, file: String?) {
            plugin.logger.info("Loading messages config..")
            messages.clear()
            val sc = SimpleConfig(file, plugin)
            for (msg in entries) {
                var ob = sc.config[msg.name.lowercase(Locale.getDefault())]
                if (ob == null && msg.defaultMsg != null) {
                    ob = msg.defaultMsg
                    sc.config[msg.name.lowercase(Locale.getDefault())] = ob
                }
                ob?.let {
                    val msgOb = Message.createMessage(ob)!!
                    msgOb.parse()
                    messages.put(msg,msgOb )
                }
            }
            try {
                sc.config.save(File(plugin.dataFolder, file))
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
            LibMessages.NO_PERMISSION.setMessage(prefix + NO_PERMISSION.message)
            plugin.logger.info("Loaded " + messages.size + " messages.")
        }
    }
}
