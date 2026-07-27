package com.example.eplmatches.domain.model

data class Match(
    val matchNumber: Int,
    val dateUtc: String,
    val status: String?,
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamScore: Int?,
    val awayTeamScore: Int?,
    val roundNumber: Int?,
    val location: String?,
    val group: String?,
    val localDate: String,
    val localTime: String
)
