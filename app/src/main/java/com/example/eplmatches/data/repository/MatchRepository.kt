package com.example.eplmatches.data.repository

import com.example.eplmatches.data.api.MatchApiService
import com.example.eplmatches.data.api.MatchDto
import com.example.eplmatches.data.local.MatchDao
import com.example.eplmatches.data.local.MatchEntity
import com.example.eplmatches.domain.model.Match
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class MatchRepository(
    private val apiService: MatchApiService,
    private val matchDao: MatchDao
) {

    fun getAllMatches(): Flow<List<Match>> {
        return matchDao.getAllMatches().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun refreshMatches() {
        try {
            val dtos = apiService.getMatches()
            val entities = dtos.map { it.toEntity() }
            matchDao.deleteAll()
            matchDao.insertAll(entities)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getMatchByNumber(matchNumber: Int): Match? {
        return matchDao.getMatchByNumber(matchNumber)?.toDomain()
    }

    suspend fun getMatchesPaged(limit: Int, offset: Int): List<Match> {
        return matchDao.getMatchesPaged(limit, offset).map { it.toDomain() }
    }

    suspend fun getCachedCount(): Int {
        return matchDao.getCount()
    }

    private fun MatchDto.toEntity(): MatchEntity {
        return MatchEntity(
            matchNumber = matchNumber ?: 0,
            dateUtc = dateUtc ?: "",
            status = status,
            homeTeam = homeTeam ?: "",
            awayTeam = awayTeam ?: "",
            homeTeamScore = homeTeamScore,
            awayTeamScore = awayTeamScore,
            roundNumber = roundNumber,
            location = location,
            group = group
        )
    }

    private fun MatchEntity.toDomain(): Match {
        val (localDate, localTime) = convertUtcToLocal(dateUtc)
        return Match(
            matchNumber = matchNumber,
            dateUtc = dateUtc,
            status = status,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            homeTeamScore = homeTeamScore,
            awayTeamScore = awayTeamScore,
            roundNumber = roundNumber,
            location = location,
            group = group,
            localDate = localDate,
            localTime = localTime
        )
    }

    private fun convertUtcToLocal(dateUtc: String): Pair<String, String> {
        return try {
            val cleaned = dateUtc.replace("Z", "").trim()
            val utcFormat = if (cleaned.contains("T")) {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            } else {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            }
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val parsed = utcFormat.parse(cleaned) ?: return Pair("", "")
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            Pair(dateFormat.format(parsed), timeFormat.format(parsed))
        } catch (e: Exception) {
            Pair("", "")
        }
    }
}
