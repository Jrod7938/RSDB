package com.gepc

import com.gepc.utils.TokenProvider
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    val token = TokenProvider.getToken()
    val kord = Kord(token)

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot != false) return@on

        CommandHandler.handleCommand(message)
    }

    kord.login {
        intents += Intent.MessageContent
    }
}
