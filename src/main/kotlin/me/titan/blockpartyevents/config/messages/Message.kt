package me.titan.blockpartyevents.config.messages

import me.titan.titanlib.common.Common

class Message(var content: MutableList<String>) {

    var parsed = false;

    // parses message controllers.
    fun parse(){
        if(parsed) return
        val parsedVersion = ArrayList<String>();
        for (msg in content) {
            parsedVersion.addAll(applyControllers(msg))
        }
        content = parsedVersion
        parsed = true
    }
    private fun applyControllers(text: String) : MutableList<String> {
        val rt = ArrayList<String>()
        var text =text
        text = applyControllerStr(text,"{center}") {Common.getCenteredMessage(text)}
        if(text.contains("{prefix}")){
            text = text.replace("{prefix}","");
            text = prefix(text)
        }
        text = applyControllerStr(text, "{chatline}") {Common.fullChatLine("")}
        val r = applyController(text, "{boxed}") { str: String ->
            val list: MutableList<String> = ArrayList()
            if (!str.contains("{boxed}")) {
                list.add(str)
                return@applyController list
            }
            list.add(Common.fullChatLine("&6"))
            list.add(str.replace("{boxed}", ""))
            list.add(Common.fullChatLine("&6"))
            list
        }
        if(r != null){
            rt.addAll(r)
        }else{
            rt.add(text);
        }

        return rt
    }


    private fun applyControllerStr(text: String, placeholder: String, toString: (String) -> String): String {
        if(text.contains(placeholder)){
            var text =text
            text = text.replace(placeholder, "");
            return toString(text)
        }
        return text
    }
    private fun applyController(text: String, placeholder: String, toString: (String) -> MutableList<String>): MutableList<String>? {
        if(text.contains(placeholder)){
            return toString(text.replace(placeholder,""));
        }
        return null
    }

    private fun prefix(str: String): String {
        if (str.contains("{noprefix}")) {
            return str.replace("{noprefix}", "")
        }
        return Messages.prefix + str
    }

    companion object
    {
        fun createMessage(ob: Any?): Message? {
            if(ob == null) return null
            if(ob is String){

                val list = ArrayList<String>()
                list.add(ob)
                return Message(list)

            }else if(ob is MutableList<*>){
                return Message(ob as MutableList<String>);
            }
            return null
        }
    }

}