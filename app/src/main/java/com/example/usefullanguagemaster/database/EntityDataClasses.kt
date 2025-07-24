package com.example.usefullanguagemaster.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/* This is a set of languages that the user may study in this app. */
@Entity(
    tableName = "languages_learning",
    indices = [Index(value = ["Language"], unique = true)]
)
data class LanguagesLearning(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")       val id: Int = 0,
    @ColumnInfo(name = "Language") val language: String
)

//----------------------------------------------------------------------------

/* This is a set of languages into which the user would like to translate words,
 phrases, etc. The user fills in this table by himself. */
@Entity(
    tableName = "languages_translation",
    indices = [Index(value = ["Language"], unique = true)]
)
data class LanguagesTranslation(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")       val id: Int = 0,
    @ColumnInfo(name = "Language") val language: String
)

//----------------------------------------------------------------------------

/* These are sets of expression. The user can create a set and add new words,
phrases etc. to it. All items that the user adds to it will be available only
within that set and in the general pool (but not in other sets). For example,
if the user adds the word 'dog' to the expression set 'Animals', then he can
work with this word in the expression set "Animals". However, the word will be
invisible to other sets (until the user adds it to a new set). At the same time,
the word 'dog' is in the general pool. So, when the user creates a new set of
expressions, he can add words from this pool. */
@Entity(
    tableName = "expression_sets",
    indices = [
        Index(value = ["Name"], unique = true),
        Index(value = ["Learning_language"]),
        Index(value = ["Translation_language"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = LanguagesLearning::class,
            parentColumns = ["Id"],
            childColumns =  ["Learning_language"]
        ),
        ForeignKey(
            entity = LanguagesTranslation::class,
            parentColumns = ["Id"],
            childColumns =  ["Translation_language"]
        )
    ]
)
data class ExpressionSets(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")                   val id: Int = 0,
    @ColumnInfo(name = "Name")                 val name: String,
    @ColumnInfo(name = "Learning_language")    val learningLanguage: Int,
    @ColumnInfo(name = "Translation_language") val translationLanguage: Int
)

//----------------------------------------------------------------------------
/* There is a table that has the next values: Word, Short phrase, Full phrase*/
@Entity(
    tableName = "expression_types",
    indices = [
        Index(value = ["Type"], unique = true)
    ]
)
data class ExpressionTypes(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")   val id: Int = 0,
    @ColumnInfo(name = "Type") val type: String
)

//----------------------------------------------------------------------------
/* Common set of expressions (words, short phrases etc.)*/
@Entity(
    tableName = "expressions",
    indices = [
        Index(value = ["Language"]),
        Index(value = ["Expression_type"]),
        Index(value = ["Expression"], unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = LanguagesLearning::class,
            parentColumns = ["Id"],
            childColumns =  ["Language"]
        ),
        ForeignKey(
            entity = ExpressionTypes::class,
            parentColumns = ["Id"],
            childColumns =  ["Expression_type"]
        )
    ]
)
data class Expressions(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")              val id: Int = 0,
    @ColumnInfo(name = "Language")        val language: Int = 1,
    @ColumnInfo(name = "Expression_type") val expressionType: Int,
    @ColumnInfo(name = "Expression")      val expression: String
)

//----------------------------------------------------------------------------
/*many to many between expressions and sets of vocabulary (words and vocabulary)*/
@Entity(
    tableName = "mapping_expressions_sets",
    indices = [
        Index(value = ["Set_id"]),
        Index(value = ["Expression_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ExpressionSets::class,
            parentColumns = ["Id"],
            childColumns =  ["Set_id"]
        ),
        ForeignKey(
            entity = Expressions::class,
            parentColumns = ["Id"],
            childColumns =  ["Expression_id"]
        )
    ]
)
data class MappingExpressionsSets(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")            val id: Int = 0,
    @ColumnInfo(name = "Set_id")        val setId: Int,
    @ColumnInfo(name = "Expression_id") val expressionId: Int
)

//----------------------------------------------------------------------------

@Entity(
    tableName = "mapping_expressions_translations",
    indices = [
        Index(value = ["Expression"]),
        Index(value = ["Translation"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Expressions::class,
            parentColumns = ["Id"],
            childColumns =  ["Expression"]
        ),
        ForeignKey(
            entity = Translations::class,
            parentColumns = ["Id"],
            childColumns =  ["Translation"]
        )
    ]
)
data class MappingExpressionsTranslations(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")          val id: Int = 0,
    @ColumnInfo(name = "Expression")  val expression: Int,
    @ColumnInfo(name = "Translation") val translation: Int
)

//----------------------------------------------------------------------------

// examples parts of speech: noun - n., verb - v., adjective - adj. etc.
@Entity(
    tableName = "parts_of_speech",
    indices = [
        Index(value = ["Language"]),
        Index(value = ["Short_name"], unique = true),
        Index(value = ["Full_name"],  unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity =        LanguagesLearning::class,
            parentColumns = ["Id"],
            childColumns =  ["Language"]
        )
    ]
)
data class PartsOfSpeech(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")         val id: Int = 0,
    @ColumnInfo(name = "Language")   val language: Int,
    @ColumnInfo(name = "Short_name") val shortName: String,
    @ColumnInfo(name = "Full_name")  val fullName: String
)

//----------------------------------------------------------------------------

//phrases to words (expressions + translation)
@Entity(
    tableName = "phrases_auxiliary",
    indices = [
        Index(value = ["Translation_language"]),
        Index(value = ["Expression_id"]),
        Index(value = ["Expression_translation"]),
        Index(value = ["Phrase"]),
        Index(value = ["Phrase_translation"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = LanguagesTranslation::class,
            parentColumns = ["Id"],
            childColumns =  ["Translation_language"]
        ),
        ForeignKey(
            entity = Expressions::class,
            parentColumns = ["Id"],
            childColumns =  ["Expression_id"]
        ),
        ForeignKey(
            entity = Translations::class,
            parentColumns = ["Id"],
            childColumns =  ["Expression_translation"]
        )
    ]
)
data class PhrasesAuxiliary(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")                     val id: Int = 0,
    @ColumnInfo(name = "Translation_language")   val translationLanguage: Int,
    @ColumnInfo(name = "Expression_id")          val expressionId: Int,
    @ColumnInfo(name = "Expression_translation") val expressionTranslation: Int,
    @ColumnInfo(name = "Phrase")                 val phrase: String,
    @ColumnInfo(name = "Phrase_translation")     val phraseTranslation: String
)

//----------------------------------------------------------------------------

@Entity(
    tableName = "translations",
    indices = [
        Index(value = ["Language"]),
        Index(value = ["Expression_id"]),
        Index(value = ["Part_of_speech"]),
        Index(value = ["Expression_type"]),
        Index(value = ["Translation"], unique = true),
    ],
    foreignKeys = [
        ForeignKey(
            entity =        LanguagesTranslation::class,
            parentColumns = ["Id"],
            childColumns =  ["Language"]
        ),
        ForeignKey(
            entity =        Expressions::class,
            parentColumns = ["Id"],
            childColumns =  ["Expression_id"]
        ),
        ForeignKey(
            entity =        PartsOfSpeech::class,
            parentColumns = ["Id"],
            childColumns =  ["Part_of_speech"]
        ),
        ForeignKey(
            entity =        ExpressionTypes::class,
            parentColumns = ["Id"],
            childColumns =  ["Expression_type"]
        )
    ]
)
data class Translations(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")              val id: Int = 0,
    @ColumnInfo(name = "Language")        val language: Int,
    @ColumnInfo(name = "Expression_id")   val expressionId: Int,
    @ColumnInfo(name = "Part_of_speech")  val partOfSpeech: Int,
    @ColumnInfo(name = "Expression_type") val expressionType: Int,
    @ColumnInfo(name = "Translation")     val translation: String
)

//----------------------------------------------------------------------------