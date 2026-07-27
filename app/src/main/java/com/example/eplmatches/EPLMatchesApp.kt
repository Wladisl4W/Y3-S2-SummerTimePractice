package com.example.eplmatches

import android.app.Application
import com.example.eplmatches.data.api.RetrofitClient
import com.example.eplmatches.data.local.MatchDatabase
import com.example.eplmatches.data.repository.MatchRepository

class EPLMatchesApp : Application() {

    lateinit var matchRepository: MatchRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val database = MatchDatabase.getInstance(this)
        val apiService = RetrofitClient.matchApiService

        matchRepository = MatchRepository(
            apiService = apiService,
            matchDao = database.matchDao()
        )
    }
}
