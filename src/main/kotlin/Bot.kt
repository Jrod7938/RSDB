/*
 * This file is part of the RuneScape Discord Bot project.
 *
 * Licensed under the MIT License. You may obtain a copy of the License at
 * https://opensource.org/licenses/MIT
 *
 * © 2024 Jancarlos Rodriguez, Omar Rodriguez
 */

package com.rsdb

import com.rsdb.utils.providers.LoggerProvider
import com.rsdb.utils.providers.TokenProvider
import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string

@OptIn(PrivilegedIntent::class)
suspend fun main(args: Array<String>) {
    val token = TokenProvider.getToken(args.getOrNull(0))
    val kord = Kord(token)
    val logger = LoggerProvider.logger

    kord.on<ReadyEvent> {
        logger.info { "Bot is ready!" }

        kord.createGlobalChatInputCommand(name = "flip", description = "Find the best flip.") {
            string(name = "item", description = "Gives price margins for the item.") { required = false }
        }

        kord.createGlobalChatInputCommand(
            name = "ge",
            description = "Search for an item in the Grand Exchange."
        ) {
            string(name = "item", description = "Name of the item to search for.") { required = true }
        }

        kord.createGlobalChatInputCommand(
            name = "highscore",
            description = "Fetch the RuneScape highscore for a player."
        ) {
            string(name = "player", description = "The name of the player.") { required = false }
        }

        kord.createGlobalChatInputCommand(
            name = "wiki",
            description = "Searches RuneScape Wiki"
        ) {
            string(name = "object", description = "The object to look up in the RuneScape Wiki.") { required = true }
        }
        kord.createGlobalChatInputCommand(
            name = "me",
            description = "Links Discord account to RuneScape account."
        ) {
            string(name = "username", description = "RuneScape Username.") { required = true }
        }
    }

    kord.handleCommands()

    kord.login { intents += Intent.MessageContent }
}
