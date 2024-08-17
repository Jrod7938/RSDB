package com.gepc.utils

import com.gepc.utils.providers.LoggerProvider
import org.jsoup.Jsoup

object UrlAnalyzer {

    private val logger = LoggerProvider.logger

    /**
     * Validates a URL by checking if it contains a specific text.
     *
     * @param url The URL to validate.
     * @param findText The text to search for in the content of the URL.
     * @return True if the URL does not contain the specified text; false otherwise.
     */
    fun validateUrl(url: String, findText: String): Boolean {
        logger.info { "Validating the URL: $url" }

        return try {
            // Connect to the URL and retrieve the document
            val document = Jsoup.connect(url).get()
            // Extract the body text of the document
            val content = document.body().text()

            // Check if the content contains the specified text
            if (content.contains(findText)) {
                logger.warn { "The URL: $url contains the text '$findText'." }
                false
            } else {
                true
            }
        } catch (exception: Exception) {
            // Log an error message if the URL cannot be accessed
            logger.error { "Failed to access the URL: $url. Exception: $exception" }
            false
        }
    }

    /**
     * Retrieves the page title from a URL.
     *
     * @param url The URL from which to retrieve the page title.
     * @return The page title if found; null otherwise.
     */
    fun getPageTitle(url: String): String? {
        return try {
            // Connect to the URL and retrieve the document
            val document = Jsoup.connect(url).get()
            // Select the element that contains the page title
            val element = document.select("span.mw-page-title-main").firstOrNull()
            val pageTitle = element?.text()

            // Log the page title if found, or a warning if not found
            if (pageTitle != null) {
                logger.info { "Found page title: $pageTitle" }
            } else {
                logger.warn { "Page title not found in $url" }
            }

            pageTitle
        } catch (exception: Exception) {
            // Log an error message if the URL cannot be accessed
            logger.error { "Failed to access the URL: $url. Exception: $exception" }
            null
        }
    }
}