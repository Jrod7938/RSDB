package com.gepc

import com.gepc.commands.FlipCommand
import com.gepc.commands.GECommand
import dev.kord.core.entity.Message

object CommandHandler {
    suspend fun handleCommand(message: Message) {
        val content = message.content

        when{
            content.startsWith("/ge", ignoreCase = true) -> GECommand.handle(message)
            content.startsWith("/flip", ignoreCase = true) -> FlipCommand.handle(message)
        }
    }
}