package com.example.usefullanguagemaster.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UlmDao {
    @Query("SELECT * FROM languages_learning")
    fun getAllLanguagesLearning(): Flow<List<LanguagesLearning>>
}