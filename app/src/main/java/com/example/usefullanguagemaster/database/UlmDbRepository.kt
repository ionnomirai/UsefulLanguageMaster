package com.example.usefullanguagemaster.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.usefullanguagemaster.dataClasses.NewItem
import com.example.usefullanguagemaster.enums.LanguagesLearningEnum
import kotlinx.coroutines.flow.Flow

class UlmDbRepository private constructor(context: Context) {

    private val databaseName = "useful_language_master_database"
    private val tag = "UlmDbRepository_tag"

    private val expTypes = listOf(
        "Word",
        "Short phrase",
        "Full phrase"
    )

    private val pos = listOf(
        PartsOfSpeech(language = 1, shortName = "emp.", fullName = "Empty"),
        PartsOfSpeech(language = 1, shortName = "n.", fullName = "Noun"),
        PartsOfSpeech(language = 1, shortName = "v.", fullName = "Verb"),
        PartsOfSpeech(language = 1, shortName = "adj.", fullName = "Adjective"),
        PartsOfSpeech(language = 1, shortName = "adv.", fullName = "Adverb"),
        PartsOfSpeech(language = 1, shortName = "pron.", fullName = "Pronoun"),
        PartsOfSpeech(language = 1, shortName = "prep.", fullName = "Preposition"),
        PartsOfSpeech(language = 1, shortName = "conj.", fullName = "Conjunction"),
        PartsOfSpeech(language = 1, shortName = "int.", fullName = "Interjection"),
    )

    private val database: UlmDatabase =
        Room
            .databaseBuilder(
                context = context,
                klass = UlmDatabase::class.java,
                name = databaseName
            )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d(tag, "callback --> OnCreate()")

                    // add data into language_learning table (LanguagesLearning data class)
                    db.execSQL("INSERT INTO languages_learning (Language) VALUES ('English')")

                    // add data into expression_types table (ExpressionTypes data class)
                    insertExpTypes(db, expTypes)

                    // add data into part of speech table (PartsOfSpeech data class)
                    insertPOS(db, pos)
                }
            })
            .build()

    companion object {
        private var INSTANCE: UlmDbRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = UlmDbRepository(context)
            }
        }

        fun get(): UlmDbRepository {
            return INSTANCE
                ?: throw IllegalStateException("The local useful_language_master_database is not initialized.")
        }
    }

    //----------------------------Raw default insert-------------------------
    /* default insert data into expression_types table (ExpressionTypes data class) */
    private fun insertExpTypes(db: SupportSQLiteDatabase, items: List<String>) {
        items.forEach { type ->
            db.execSQL("INSERT INTO expression_types (Type) VALUES (?)", arrayOf(type))
        }
    }

    /* default insert data into part of speech table (PartsOfSpeech data class) */
    private fun insertPOS(db: SupportSQLiteDatabase, items: List<PartsOfSpeech>) {
        items.forEach {
            db.execSQL(
                "INSERT INTO parts_of_speech (Language, Short_name, Full_name) VALUES (?, ?, ?)",
                arrayOf(it.language, it.shortName, it.fullName)
            )
        }
    }
    //-----------------------------------------------------------------------

    //----------------------------Get default data (until it is static)-------------------------
    //fun getExpTypes(): List<String> = expTypes
    //-----------------------------------------------------------------------

    fun getAllLanguagesLearning(): Flow<List<LanguagesLearning>> =
        database.ulmDao().getAllLanguagesLearning()

    // ExpressionSets (expression_sets), but with full string information without foreign keys.
    fun getAllExpSetsText(): Flow<List<ExpressionSetsTextData>> =
        database.ulmDao().getAllExpSetsText()

    suspend fun getAllExpSetsTextOnce(): List<ExpressionSetsTextData> =
        database.ulmDao().getAllExpSetsTextOnce()

    // Get Id from language_translation table. If there is no such record, return null.
    private suspend fun getLanguageTranslation(languageName: String): Int? =
        database.ulmDao().getLanguageTranslation(languageName)

    // Insert new language in languages_translation table
    private suspend fun insertLanguageTranslation(item: LanguagesTranslation): Long =
        database.ulmDao().insertLanguageTranslation(item)

    // Insert expression set. But this is raw function, only for developers.
    private suspend fun insertExpressionSetDev(item: ExpressionSets) =
        database.ulmDao().insertExpressionSetDev(item)

    // Insert to many to many: element to set
    private suspend fun insertMES(mes: MappingExpressionsSets) =
        database.ulmDao().insertMES(mes)

    // Insert to many to many: element to translation
    private suspend fun insertMET(met: MappingExpressionsTranslations) =
        database.ulmDao().insertMET(met)

    suspend fun getAllExpressionsTest() = database.ulmDao().getAllExpressionsTest()

    suspend fun insertExpressionSet(
        name: String,
        languageLearning: LanguagesLearningEnum,
        languageTranslation: String
    ) {
        database.withTransaction {
            // Get language translation Id or null otherwise (if there is no row with this name)
            var languageTranslationId = getLanguageTranslation(languageTranslation)

            // if language translation id is null, we should create this row (insert new language translation)
            if (languageTranslationId == null) {
                // insert row and get id of this new row (with language)
                val languageTranslationIdNew =
                    insertLanguageTranslation(LanguagesTranslation(language = languageTranslation))
                // Additional check
                if (languageTranslationIdNew != -1L) {
                    languageTranslationId = languageTranslationIdNew.toInt()
                } else {
                    languageTranslationId = getLanguageTranslation(languageTranslation)
                        ?: throw IllegalStateException("Failed to get languageId.")
                }
            }
            // insert ExpressionSet
            insertExpressionSetDev(
                ExpressionSets(
                    name = name,
                    learningLanguage = languageLearning.id,
                    translationLanguage = languageTranslationId
                )
            )
        }
    }

    suspend fun getPOS() = database.ulmDao().getPOS()

    suspend fun getExpTypes() = database.ulmDao().getExpTypes()

    suspend fun getExpSet(id:Int) = database.ulmDao().getExpressionSet(id)

    //-------------------------------save expression------------------------------------
    suspend fun saveExpression(
        expression: NewItem,
        languageLearning: Int,
        languageTranslation: Int,
        expressionType: Int,
        pos: Int,
        setId: Int
    ) {
        /* Security indicators. If both indicators are true, so we don't need to
        * add this information to the mes and to the met.*/
        var indicators: Pair<Boolean, Boolean> = (false to false)

        // Insert new expression. If insertion is ok, return id. Otherwise return -1
        var expressionId = database.ulmDao().insertExpression(
            Expressions(
                language = languageLearning,
                expressionType = expressionType,
                expression = expression.item.first
            )
        )

        // get the Id of the element if it already exists
        if (expressionId == -1L) {
            indicators.copy(first = true)
            expressionId = database.ulmDao()
                .getExpressionId(languageLearning, expressionType, expression.item.first).toLong()
        }

        // Insert new translation. If insertion is ok, return id. Otherwise return -1
        var translationId = database.ulmDao().insertTranslation(
            Translations(
                language = languageLearning,
                expressionId = expressionId.toInt(),
                partOfSpeech = pos,
                expressionType = expressionType,
                translation = expression.item.second
            )
        )

        // get the Id of the translation if it already exists
        if (translationId == -1L){
            indicators.copy(second = true)
            translationId = database.ulmDao().getTranslationId(
                languageTranslation, expressionId.toInt(), pos, expressionType, expression.item.second
            ).toLong()
        }

        // Insert phrases
        expression.phrases?.let { phrases ->
            phrases.forEach {
                database.ulmDao().insertPhrase(
                    PhrasesAuxiliary(
                        translationLanguage = languageTranslation,
                        expressionId = expressionId.toInt(),
                        expressionTranslation = translationId.toInt(),
                        phrase = it.first,
                        phraseTranslation = it.second)
                )
            }
        }

        // do the next steps, only if it make sense. If we don't have these entries in met or mes.
        if (indicators.first == false || indicators.second == false){
            // insert mapping mes (relationship between an element and a set)
            insertMES(MappingExpressionsSets(setId = setId, expressionId = expressionId.toInt()))

            // insert mapping met
            insertMET(MappingExpressionsTranslations(
                expression = expressionId.toInt(),
                translation = translationId.toInt()))
        }
    }
}