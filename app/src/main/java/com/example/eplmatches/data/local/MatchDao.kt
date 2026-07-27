package com.example.eplmatches.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Query("SELECT * FROM matches ORDER BY dateUtc ASC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE matchNumber = :matchNumber")
    suspend fun getMatchByNumber(matchNumber: Int): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(matches: List<MatchEntity>)

    @Query("DELETE FROM matches")
    suspend fun deleteAll()

    @Query("SELECT * FROM matches ORDER BY dateUtc ASC LIMIT :limit OFFSET :offset")
    suspend fun getMatchesPaged(limit: Int, offset: Int): List<MatchEntity>

    @Query("SELECT COUNT(*) FROM matches")
    suspend fun getCount(): Int
}
