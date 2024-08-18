package com.rsdb.utils.providers

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object TokenProvider {

    fun getToken(argToken: String? = null): String {
        val tokenPath = Paths.get("src/main/kotlin/token.txt")

        // use token from argument
        return argToken?.takeIf { it.isNotBlank() }
        // If no argument, use token from file or environment variable
            ?: if (Files.exists(tokenPath) && Files.size(tokenPath) > 0) {
                File(tokenPath.toUri()).readText().trim()
            } else {
                System.getenv("DISCORD_BOT_TOKEN") ?: error("Discord bot token is missing.")
            }
    }
}
