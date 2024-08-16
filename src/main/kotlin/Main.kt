package com.gepc

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    val token: String = getToken()
    val kord = Kord(token)

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot != false) return@on

        // check if our command is being invoked
        if (!message.content.startsWith("/priceCheck")) return@on

        // all clear, give them the pong!
        message.channel.createMessage("TODO: Not Implemented yet.")
    }

    kord.login {
        // we need to specify this to receive the content of messages
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}

fun getToken(): String {
    val tokenPath = Paths.get("src/main/kotlin/token.txt")

    return if (Files.exists(tokenPath) && Files.size(tokenPath) > 0) {
        File(tokenPath.toUri()).readText().trim()
    } else {
        System.getenv("DISCORD_BOT_TOKEN") ?: error("Discord bot token is missing.")
    }
}
