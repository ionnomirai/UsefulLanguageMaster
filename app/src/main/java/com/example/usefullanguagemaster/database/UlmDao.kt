package com.example.usefullanguagemaster.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UlmDao {
    @Query("SELECT * FROM languages_learning")
    fun getAllLanguagesLearning(): Flow<List<LanguagesLearning>>

    // ExpressionSets (expression_sets), but with full string information without foreign keys.
    @Query("SELECT es.Id AS 'id', es.Name AS 'name', ll.Language AS 'languageLearning', lt.Language AS 'languageTranslation' " +
            "FROM languages_learning AS ll " +
            "INNER JOIN expression_sets AS es ON es.Learning_language = ll.id " +
            "INNER JOIN languages_translation AS lt ON lt.id = es.Translation_language")
    fun getAllExpSetsText(): Flow<List<ExpressionSetsTextData>>
    
    @Query("SELECT es.Id AS 'id', es.Name AS 'name', ll.Language AS 'languageLearning', lt.Language AS 'languageTranslation' " +
            "FROM languages_learning AS ll " +
            "INNER JOIN expression_sets AS es ON es.Learning_language = ll.id " +
            "INNER JOIN languages_translation AS lt ON lt.id = es.Translation_language")
    suspend fun getAllExpSetsTextOnce(): List<ExpressionSetsTextData>

    /* Get Id from language_translation table. If there is no such record, return null. We don't use
    * 'LIMIT 1' because the language field must be Unique. */
    @Query("SELECT Id FROM languages_translation AS 'lt' WHERE lt.Language = :languageName LIMIT 1")
    suspend fun getLanguageTranslation(languageName: String): Int?

    /* Insert new row (language translation) into language_translation table */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLanguageTranslation(item: LanguagesTranslation): Long

    @Insert
    suspend fun insertExpressionSetDev(item: ExpressionSets)
}