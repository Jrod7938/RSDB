/*
 * This file is part of the RuneScape Discord Bot project.
 *
 * Licensed under the MIT License. You may obtain a copy of the License at
 * https://opensource.org/licenses/MIT
 *
 * © 2024 Jancarlos Rodriguez, Omar Rodriguez
 */

package com.rsdb.commands

import com.rsdb.services.ItemDetailsService
import com.rsdb.utils.providers.LoggerProvider
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent

/**
 * Command handler for the "ge" command in the RuneScape Discord bot.
 *
 * This command retrieves and displays details about a specific item from the RuneScape Grand Exchange.
 */
object GECommand {

    private val logger = LoggerProvider.logger
    private val itemDetailsService = ItemDetailsService()

    /**
     * Handles the execution of the "ge" command.
     *
     * This method fetches details about the specified item from the RuneScape Grand Exchange and
     * responds to the user with the information.
     *
     * @param event The event triggered by the "ge" command.
     */
    suspend fun handle(event: ChatInputCommandInteractionCreateEvent) {
        logger.info { "Handling GE command for item: ${event.interaction.command.rootName}" }

        val itemName = event.interaction.command.strings["item"] ?: run {
            logger.warn { "Item name not provided in command" }
            return
        }

        val itemMessage = itemDetailsService.getItemDetailsMessage(itemName)
        event.interaction.respondPublic { content = itemMessage }
        logger.info { "Response sent to Discord" }
    }
}
