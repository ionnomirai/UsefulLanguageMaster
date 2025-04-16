package com.example.usefullanguagemaster.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        LanguagesLearning::class,
        LanguagesTranslation::class,
        ExpressionSets::class
    ],
    version = 1
)
abstract class UlmDatabase: RoomDatabase() {
    abstract fun ulmDao(): UlmDao
}