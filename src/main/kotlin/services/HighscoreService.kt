package com.rsdb.services

import com.rsdb.models.HighscoreEntry
import com.rsdb.models.Skill
import com.rsdb.utils.providers.HttpClientProvider
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A service that retrieves and processes RuneScape highscore data for a given player.
 *
 * This service uses the RuneScape Hiscores API to fetch a player's highscore data and parses
 * the response into a map of [Skill] to [HighscoreEntry].
 */
object HighscoreService {

    /**
     * Fetches the hiscores for a given player from the RuneScape Hiscores API.
     *
     * This method makes an HTTP request to the RuneScape Hiscores API and parses the response into
     * a map of [Skill] to [HighscoreEntry], where each entry represents the player's rank, level,
     * and experience in a specific skill.
     *
     * @param playerName The name of the player whose hiscores are to be fetched.
     * @return A map of [Skill] to [HighscoreEntry], or `null` if the fetch or parsing fails.
     */
    suspend fun getHiscores(playerName: String): Map<Skill, HighscoreEntry>? {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse =
                    HttpClientProvider.client.get("https://secure.runescape.com/m=hiscore/index_lite.ws?player=$playerName")
                val responseBody = response.bodyAsText()

                return@withContext parseResponse(responseBody)
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

    /**
     * Parses the response from the RuneScape Hiscores API into a map of [Skill] to [HighscoreEntry].
     *
     * The response is expected to be a CSV-like string where each line corresponds to a skill.
     * The line format is "rank,level,experience".
     *
     * @param responseBody The response body from the Hiscores API as a [String].
     * @return A map of [Skill] to [HighscoreEntry], or `null` if the parsing fails.
     */
    private fun parseResponse(responseBody: String): Map<Skill, HighscoreEntry>? {
        return responseBody.lines().mapIndexedNotNull { index, line ->
            val parts = line.split(",")
            if (parts.size == 3) {
                val rank = parts[0].toIntOrNull() ?: return@mapIndexedNotNull null
                val level = parts[1].toIntOrNull() ?: return@mapIndexedNotNull null
                val experience = parts[2].toLongOrNull() ?: return@mapIndexedNotNull null
                val entry = HighscoreEntry(rank, level, experience)
                val skill = Skill.fromIndex(index) ?: return@mapIndexedNotNull null
                skill to entry
            } else {
                null
            }
        }.toMap()
    }
}
