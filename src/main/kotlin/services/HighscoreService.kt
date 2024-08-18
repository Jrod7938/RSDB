package com.rsdb.services

import com.rsdb.models.HighscoreEntry
import com.rsdb.models.Skill
import com.rsdb.utils.providers.HttpClientProvider
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HighscoreService {

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
