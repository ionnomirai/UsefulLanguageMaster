package com.example.usefullanguagemaster.database

import androidx.room.Dao
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
}