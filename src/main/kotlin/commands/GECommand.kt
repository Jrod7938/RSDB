package com.gepc.commands

import com.gepc.services.ItemDetailsService
import com.gepc.utils.providers.LoggerProvider
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent

object GECommand {

    private val logger = LoggerProvider.logger
    private val itemDetailsService = ItemDetailsService()

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
