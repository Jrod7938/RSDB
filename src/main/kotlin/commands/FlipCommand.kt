package com.gepc.commands

import dev.kord.core.entity.Message

object FlipCommand {

    suspend fun handle(message: Message) {
        // TODO: Implement flip margins fetching logic
        message.channel.createMessage("Flip command is under construction.")
    }
}
