package com.example.usefullanguagemaster.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
}