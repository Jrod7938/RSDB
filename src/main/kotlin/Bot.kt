package com.gepc

import com.gepc.utils.LoggerProvider
import com.gepc.utils.TokenProvider
import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    val token = TokenProvider.getToken()
    val kord = Kord(token)
    val logger = LoggerProvider.logger

    kord.on<ReadyEvent> {
        logger.info { "Bot is ready!" }

        kord.createGlobalChatInputCommand(
            name = "flip",
            description = "Gives price margins for the item."
        ) {
            string(name = "item", description = "Name of the item to search for.")
        }

        kord.createGlobalChatInputCommand(
            name = "ge",
            description = "Search for an item in the Grand Exchange."
        ) {
            string(name = "item", description = "Name of the item to search for.")
        }
    }

    kord.handleCommands()

    kord.login {
        intents += Intent.MessageContent
    }
}
