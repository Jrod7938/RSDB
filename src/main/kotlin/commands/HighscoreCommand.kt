package com.gepc.commands

import com.gepc.models.HighscoreEntry
import com.gepc.models.Skill
import com.gepc.service.HighscoreService
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import java.text.NumberFormat
import java.util.*

object HighscoreCommand {
    suspend fun handle(event: ChatInputCommandInteractionCreateEvent) {
        val deferredResponse = event.interaction.deferPublicResponse()
        val playerName = event.interaction.command.strings["player"] ?: return
        val highscoreEntries = HighscoreService.getHiscores(playerName)

        val message = formatHighscoreResponse(playerName, highscoreEntries)

        deferredResponse.respond {
            content = message
        }
    }

    private fun formatHighscoreResponse(playerName: String, highscoreEntries: Map<Skill, HighscoreEntry>?): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return if (highscoreEntries.isNullOrEmpty()) {
            "Could not fetch highscores for player: $playerName"
        } else {
            val builder = StringBuilder("```kotlin\n")
            builder.append("Highscores for $playerName:\n")
            highscoreEntries.forEach { (skill, entry) ->
                builder.append(
                    "${skill.displayName} - Rank: ${numberFormat.format(entry.rank)}, Level: ${
                        numberFormat.format(
                            entry.level
                        )
                    }, Experience: ${numberFormat.format(entry.experience)}\n"
                )
            }
            builder.append("```")
            builder.toString()
        }
    }
}
