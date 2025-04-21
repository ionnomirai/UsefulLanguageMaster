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
    @ColumnInfo(name = "Id")
    val id: Int = 0,

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
    @ColumnInfo(name = "Id")
    val id: Int = 0,

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
            childColumns = ["Learning_language"]
        ),
        ForeignKey(
            entity = LanguagesTranslation::class,
            parentColumns = ["Id"],
            childColumns = ["Translation_language"]
        )
    ]
)
data class ExpressionSets(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    val id: Int = 0,

    @ColumnInfo(name = "Name") val name: String,
    @ColumnInfo(name = "Learning_language") val learningLanguage: Int,
    @ColumnInfo(name = "Translation_language") val translationLanguage: Int
)
