package com.gepc.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object TokenProvider {

    fun getToken(): String {
        val tokenPath = Paths.get("src/main/kotlin/token.txt")
        return if (Files.exists(tokenPath) && Files.size(tokenPath) > 0) {
            File(tokenPath.toUri()).readText().trim()
        } else {
            System.getenv("DISCORD_BOT_TOKEN") ?: error("Discord bot token is missing.")
        }
    }
}
