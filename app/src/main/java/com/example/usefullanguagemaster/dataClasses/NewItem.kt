package com.example.usefullanguagemaster.dataClasses

/* The class represents a new item (a word, a phrase, or an expression) that needs
to be added to the database.*/
data class NewItem(
    val type:                   String,                             // name
    val item:                   Pair<String, String>,               // <item, translation>
    var pos:                    String? = null,                     // name
    var phrases:                List<Pair<String, String>>? = null, // <phrase, translation>
) {
    override fun toString(): String {
        return "Type:                    name: ${type}\n" +
                "Item:                    text=${item.first} -- translation: ${item.second}\n" +
                "Pos:                     name: ${pos ?: "empty"}\n" +
                "Phrase:                  phrase: ${if(phrases != null) phrases.toString() else "empty"}"
    }
}


/*
data class NewItem(
    val type:                   Pair<Int, String>,            // <id, name>
    val item:                   Pair<String, String>,         // <item, translation>
    var pos:                    Pair<Int, String>? = null,    // <id, name>
    var phrase:                 Pair<String, String>? = null, // <phrase, translation>
    var additionalTranslations: List<String>? = null,         // <translation>
    var additionalPhrases:      List<String>? = null          // <translation>
) {
    override fun toString(): String {
        return "Type:                     id=${type.first} -- text: ${type.second}/n" +
                "Item:                    id=${item.first} -- text: ${item.second}/n" +
                "Pos:                     id=${pos?.first?:"empty"} -- text: ${pos?.second ?: "empty"}/n" +
                "Phrase:                  id=${phrase?.first?:"empty"} -- text: ${phrase?.second ?: "empty"}/n" +
                "Additional translations: ${if(additionalTranslations != null) additionalTranslations.toString() else "empty"}/n" +
                "Additional phrases:      ${if(additionalPhrases != null) additionalPhrases.toString() else "empty"}"
    }
}
*/
