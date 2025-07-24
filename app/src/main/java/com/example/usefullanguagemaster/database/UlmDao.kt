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

    @Query("SELECT * FROM parts_of_speech")
    suspend fun getPOS(): List<PartsOfSpeech>

    @Query("SELECT * FROM expression_types")
    suspend fun getExpTypes(): List<ExpressionTypes>

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

    @Query("SELECT *" +
            "FROM expression_sets AS 'es'" +
            "WHERE es.Id = :id")
    suspend fun getExpressionSet(id: Int): ExpressionSets

    /* Get Id from language_translation table. If there is no such record, return null. We don't use
    * 'LIMIT 1' because the language field must be Unique. */
    @Query("SELECT Id FROM languages_translation AS 'lt' WHERE lt.Language = :languageName LIMIT 1")
    suspend fun getLanguageTranslation(languageName: String): Int?

    @Query("SELECT Id " +
            "FROM expressions AS 'e' " +
            "WHERE e.Language = :language AND e.Expression_type = :expType AND e.Expression = :text " +
            "LIMIT 1")
    suspend fun getExpressionId(language: Int, expType: Int, text: String): Int

    @Query("SELECT Id " +
            "FROM translations AS 't'" +
            "WHERE t.Language = :languageTr AND t.Expression_id = :expId AND t.Part_of_speech = :pos " +
            "AND t.Expression_type = :expType AND t.Translation = :text " +
            "LIMIT 1")
    suspend fun getTranslationId(languageTr: Int, expId: Int, pos: Int, expType: Int, text: String): Int

    @Query("SELECT *" +
            "FROM expressions")
    suspend fun getAllExpressionsTest(): List<Expressions>

    /* Insert new row (language translation) into language_translation table */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLanguageTranslation(item: LanguagesTranslation): Long

    @Insert
    suspend fun insertExpressionSetDev(item: ExpressionSets)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpression(item: Expressions): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTranslation(item: Translations): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhrase(item: PhrasesAuxiliary)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMES(mes: MappingExpressionsSets)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMET(met: MappingExpressionsTranslations)

}