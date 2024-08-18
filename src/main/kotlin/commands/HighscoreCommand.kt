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
     * The method first checks if the command includes a specific player name. If not, it attempts to
     * use the player's RuneScape username linked to their Discord profile (stored in the `userProfiles` map).
     * If neither is available, the command will respond with an error message.
     *
     * @param event The event triggered by the "highscore" command.
     * @param userProfiles A mutable map linking Discord user IDs to their linked RuneScape usernames.
     */
    suspend fun handle(event: ChatInputCommandInteractionCreateEvent, userProfiles: MutableMap<String, String>) {
        val deferredResponse = event.interaction.deferPublicResponse()
        val userId = event.interaction.user.id.toString()

        // Check if the player name is provided or linked to the Discord ID
        val playerName = event.interaction.command.strings["player"] ?: userProfiles[userId]

        if (playerName.isNullOrEmpty()) {
            // If no player name is provided and no profile is linked, respond with an error message
            deferredResponse.respond {
                content =
                    "You haven't linked a RuneScape profile to your Discord account. Please use the `/me` command to link your account or specify a player name."
            }
            return
        }

        // Fetch the highscore if the player name is available
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
