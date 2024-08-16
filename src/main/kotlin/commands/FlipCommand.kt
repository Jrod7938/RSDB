package com.gepc.commands

import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent

object FlipCommand {

    suspend fun handle(event: ChatInputCommandInteractionCreateEvent) {
        // TODO: Implement flip margins fetching logic
        event.interaction.respondPublic {
            content = "Flip command is under construction."
        }
    }
}
