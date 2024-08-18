package com.rsdb.commands

import com.rsdb.utils.UrlAnalyzer
import com.rsdb.utils.providers.LoggerProvider
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent

/**
 * Command handler for the "wiki" command in the RuneScape Discord bot.
 *
 * This command searches the RuneScape Wiki for a specified object and returns a link to the
 * corresponding page. It validates the URL to ensure it leads to a valid page and responds
 * with either the page title or the object name.
 */
object WikiCommand {

    private val logger = LoggerProvider.logger

    // Base URL for the RuneScape wiki
    private const val WIKI_URL = "https://runescape.wiki/w/"
    // Default message to show if the page does not contain interesting content
    private const val ERROR_PAGE_MESSAGE = "Nothing interesting happens."
    // CSS selector for extracting the page title from the HTML
    private const val PAGE_TITLE_CSS_SELECTOR = "span.mw-page-title-main"

    /**
     * Handles the chat input command event, validates the URL, and sends an appropriate response.
     *
     * This method builds a URL based on the object name provided in the command, checks if the
     * URL points to a valid page, and sends a response with either the page title or an error
     * message.
     *
     * @param event The event containing the interaction and command data.
     */
    suspend fun handle(event: ChatInputCommandInteractionCreateEvent) {
        // Defer the public response to handle the command asynchronously
        val response = event.interaction.deferPublicResponse()

        // Extract and trim the object name from the command; respond with an error if missing
        val objectName = event.interaction.command.strings["object"]?.trim() ?: run {
            logger.warn("No object name provided in the command.")
            response.respond { content = "Object name is required." }
            return
        }

        logger.info("Running Wiki search for '$objectName'")

        // Build the URL for the given object name
        val url = buildWikiUrl(objectName)

        // Validate the URL and respond based on its validity
        val isValidUrl = UrlAnalyzer.validateUrl(url, ERROR_PAGE_MESSAGE)
        if (isValidUrl) {
            // If the URL is valid, respond with the formatted message
            response.respond { content = wikiCommandResponse(objectName, url) }
        } else {
            // If the URL is invalid, respond with an error message
            response.respond { content = "Could not find a valid URL for '$objectName'.\n" +
                    "Please check the object name and try again." }
        }
    }

    /**
     * Builds the full URL for the given object name by appending it to the base URL.
     *
     * @param objectName The name of the object to include in the URL.
     * @return The full URL as a string, combining the base URL and the object name.
     */
    private fun buildWikiUrl(objectName: String): String {
        return "$WIKI_URL${objectName.replace(" ", "_")}"
    }

    /**
     * Generates a response message with either the page title or the object name.
     *
     * This method extracts the title of the page at the given URL using a CSS selector. If the
     * title is found, it is used in the response; otherwise, the object name is used.
     *
     * @param objectName The name of the object to display if no page title is found.
     * @param url The URL to include in the response.
     * @return A formatted string containing either the page title or the object name, with the URL embedded.
     */
    private fun wikiCommandResponse(objectName: String, url: String): String {
        // Retrieve the page title from the given URL
        val pageTitle = UrlAnalyzer.extractTextFromHTML(url, PAGE_TITLE_CSS_SELECTOR)

        // Return a message with the page title if available; otherwise, use the object name
        return if (pageTitle.isNullOrEmpty()) {
            "[$objectName]($url)"
        } else {
            "[$pageTitle]($url)"
        }
    }
}
