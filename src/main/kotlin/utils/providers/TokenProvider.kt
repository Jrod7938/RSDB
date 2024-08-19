/*
 * This file is part of the RuneScape Discord Bot project.
 *
 * Licensed under the MIT License. You may obtain a copy of the License at
 * https://opensource.org/licenses/MIT
 *
 * © 2024 Jancarlos Rodriguez, Omar Rodriguez
 */

package com.rsdb.utils.providers

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Provides a method to retrieve the Discord bot token used for authentication.
 *
 * The token can be provided through a command-line argument, a file, or an environment variable.
 *
 * - If a token is passed as an argument, it is used if non-null and non-blank.
 * - If no argument is provided, the token is read from a file located at `src/main/kotlin/token.txt`.
 * - If the file does not exist or is empty, the token is retrieved from the environment variable `DISCORD_BOT_TOKEN`.
 *
 * @throws IllegalStateException if no token is found in the argument, file, or environment variable.
 *
 * Example usage:
 * ```
 * val token = TokenProvider.getToken(args.getOrNull(0))
 * ```
 */
object TokenProvider {

    /**
     * Retrieves the Discord bot token.
     *
     * @param argToken Optional token passed as a command-line argument.
     * @return The Discord bot token as a [String].
     * @throws IllegalStateException if no token is found in the argument, file, or environment variable.
     */
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
