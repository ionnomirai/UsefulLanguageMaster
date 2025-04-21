package com.example.usefullanguagemaster.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.usefullanguagemaster.enums.LanguagesLearningEnum
import kotlinx.coroutines.flow.Flow

class UlmDbRepository private constructor(context: Context) {

    private val databaseName = "useful_language_master_database"
    private val tag = "UlmDbRepository_tag"

    private val database: UlmDatabase =
        Room
            .databaseBuilder(
                context = context,
                klass =   UlmDatabase::class.java,
                name =    databaseName
            )
            .addCallback(object : RoomDatabase.Callback(){
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    Log.d(tag, "callback --> OnCreate()")
                    db.execSQL("INSERT INTO languages_learning (Language) VALUES ('English')")
                }
            })
            .build()

    companion object{
        private var INSTANCE: UlmDbRepository? = null

        fun initialize(context: Context){
            if(INSTANCE == null){
                INSTANCE = UlmDbRepository(context)
            }
        }

        fun get(): UlmDbRepository{
            return INSTANCE ?: throw IllegalStateException("The local useful_language_master_database is not initialized.")
        }
    }


    fun getAllLanguagesLearning(): Flow<List<LanguagesLearning>> = database.ulmDao().getAllLanguagesLearning()

    // ExpressionSets (expression_sets), but with full string information without foreign keys.
    fun getAllExpSetsText(): Flow<List<ExpressionSetsTextData>> = database.ulmDao().getAllExpSetsText()

    // Get Id from language_translation table. If there is no such record, return null.
    private suspend fun getLanguageTranslation(languageName: String): Int? = database.ulmDao().getLanguageTranslation(languageName)

    // Insert new language in languages_translation table
    private suspend fun insertLanguageTranslation(item: LanguagesTranslation): Long =
        database.ulmDao().insertLanguageTranslation(item)

    // Insert expression set. But this is raw function, only for developers.
    private suspend fun insertExpressionSetDev(item: ExpressionSets) = database.ulmDao().insertExpressionSetDev(item)

    suspend fun insertExpressionSet(name: String, languageLearning: LanguagesLearningEnum, languageTranslation: String){
        database.withTransaction {
            // Get language translation Id or null otherwise (if there is no row with this name)
            var languageTranslationId = getLanguageTranslation(languageTranslation)

            // if language translation id is null, we should create this row (insert new language translation)
            if (languageTranslationId == null){
                // insert row and get id of this new row (with language)
                val languageTranslationIdNew = insertLanguageTranslation(LanguagesTranslation(language = languageTranslation))
                // Additional check
                if(languageTranslationIdNew != -1L){
                    languageTranslationId = languageTranslationIdNew.toInt()
                } else {
                    languageTranslationId = getLanguageTranslation(languageTranslation)
                        ?: throw IllegalStateException("Failed to get languageId.")
                }

                // insert ExpressionSet
                insertExpressionSetDev(ExpressionSets(
                    name = name,
                    learningLanguage = languageLearning.id,
                    translationLanguage = languageTranslationId
                ))
            }
        }
    }
}