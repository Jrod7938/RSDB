package com.rsdb

import com.rsdb.commands.*
import dev.kord.core.Kord
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on

// In-memory map to store user profiles (Discord ID -> RuneScape Username)
val userProfiles: MutableMap<String, String> = mutableMapOf<String, String>()

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
 * - "highscore": Fetches the RuneScape highscore for a player. If a player name is not provided,
 *   it will use the RuneScape profile linked to the user's Discord account.
 * - "wiki": Searches the RuneScape Wiki.
 * - "me": Links a RuneScape profile to the user's Discord account.
 *
 * Example usage:
 * ```
 * kord.handleCommands()
 * ```
 */
fun Kord.handleCommands() {
    on<ChatInputCommandInteractionCreateEvent> {
        when (interaction.command.rootName) {
            "flip" -> FlipCommand.handle(this)
            "ge" -> GECommand.handle(this)
            "highscore" -> HighscoreCommand.handle(this, userProfiles)
            "wiki" -> WikiCommand.handle(this)
            "me" -> MeCommand.handle(this, userProfiles)
        }
    }
}
