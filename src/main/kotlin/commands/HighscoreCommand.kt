package com.rsdb.commands

import com.rsdb.models.HighscoreEntry
import com.rsdb.models.Skill
import com.rsdb.services.HighscoreService
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import java.text.NumberFormat
import java.util.*

/**
 * Command handler for the "highscore" command in the RuneScape Discord bot.
 *
 * This command fetches and displays the RuneScape highscores for a specified player.
 */
object HighscoreCommand {

    /**
     * Handles the execution of the "highscore" command.
     *
     * This method fetches the highscores for the specified player from the RuneScape Hiscores API
     * and responds to the user with the formatted results.
     *
     * @param event The event triggered by the "highscore" command.
     */
    suspend fun handle(event: ChatInputCommandInteractionCreateEvent) {
        val deferredResponse = event.interaction.deferPublicResponse()
        val playerName = event.interaction.command.strings["player"] ?: return
        val highscoreEntries = HighscoreService.getHiscores(playerName)

        val message = formatHighscoreResponse(playerName, highscoreEntries)

        deferredResponse.respond {
            content = message
        }
    }

    /**
     * Formats the highscore data into a message string.
     *
     * This method creates a formatted message with the player's highscores, including their
     * rank, level, and experience for each skill. If no highscores are found, a message
     * indicating failure to fetch the data is returned.
     *
     * @param playerName The name of the player whose highscores are being fetched.
     * @param highscoreEntries A map of [Skill] to [HighscoreEntry] representing the player's highscores.
     * @return A formatted string with the player's highscores or an error message if the data could not be fetched.
     */
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
                        numberFormat.format(entry.level)
                    }, Experience: ${numberFormat.format(entry.experience)}\n"
                )
            }
            builder.append("```")
            builder.toString()
        }
    }
}
