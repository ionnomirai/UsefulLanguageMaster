package com.example.usefullanguagemaster

import android.app.Application
import com.example.usefullanguagemaster.database.UlmDbRepository

class UlmApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // create or start db when app starts
        UlmDbRepository.initialize(this)
    }
}