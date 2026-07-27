package com.example.eplmatches.data.api

import com.google.gson.annotations.SerializedName

data class MatchDto(
    @SerializedName("DateUtc")
    val dateUtc: String? = null,

    @SerializedName("Status")
    val status: String? = null,

    @SerializedName("HomeTeam")
    val homeTeam: String? = null,

    @SerializedName("AwayTeam")
    val awayTeam: String? = null,

    @SerializedName("HomeTeamScore")
    val homeTeamScore: Int? = null,

    @SerializedName("AwayTeamScore")
    val awayTeamScore: Int? = null,

    @SerializedName("MatchNumber")
    val matchNumber: Int? = null,

    @SerializedName("RoundNumber")
    val roundNumber: Int? = null,

    @SerializedName("Location")
    val location: String? = null,

    @SerializedName("Group")
    val group: String? = null
)
