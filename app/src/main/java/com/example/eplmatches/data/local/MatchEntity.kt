package com.example.eplmatches.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey
    val matchNumber: Int,
    val dateUtc: String,
    val status: String?,
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamScore: Int?,
    val awayTeamScore: Int?,
    val roundNumber: Int?,
    val location: String?,
    val group: String?
)
