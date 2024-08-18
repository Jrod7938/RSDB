package com.rsdb

import com.rsdb.commands.FlipCommand
import com.rsdb.commands.GECommand
import com.rsdb.commands.HighscoreCommand
import com.rsdb.commands.WikiCommand
import dev.kord.core.Kord
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on

/**
 * Extension function to handle global chat commands for the RuneScape Discord bot.
 *
 * This function listens for `ChatInputCommandInteractionCreateEvent` events and
 * delegates the command execution to the appropriate command handler based on the
 * command name.
 *
 * Supported commands:
 * - "flip": Finds the best flip for an item.
 * - "ge": Searches for an item in the Grand Exchange.
 * - "highscore": Fetches the RuneScape highscore for a player.
 * - "wiki": Searches the RuneScape Wiki.
 *
 * Example usage:
 * ```
 * kord.handleCommands()
 * ```
 */
suspend fun Kord.handleCommands() {
    on<ChatInputCommandInteractionCreateEvent> {
        when (interaction.command.rootName) {
            "flip" -> FlipCommand.handle(this)
            "ge" -> GECommand.handle(this)
            "highscore" -> HighscoreCommand.handle(this)
            "wiki" -> WikiCommand.handle(this)
        }
    }
}
