package com.rsdb.utils

import com.rsdb.utils.providers.LoggerProvider
import org.jsoup.Jsoup

object UrlAnalyzer {

    private val logger = LoggerProvider.logger

    /**
     * Validates the content of a URL by checking if it contains a specific text.
     *
     * @param url The URL to validate.
     * @param findText The text to search for in the content of the URL.
     * @return `true` if the specified text is not present in the URL content; `false` otherwise.
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
     * Extracts a specific element's text from a URL based on a CSS selector.
     *
     * @param url The URL from which to retrieve the element.
     * @param cssSelector The CSS selector used to locate the desired element.
     * @return The text content of the selected element if found; `null` otherwise.
     */
    fun extractTextFromHTML(url: String, cssSelector: String): String? {
        return try {
            // Connect to the URL and retrieve the document
            val document = Jsoup.connect(url).get()
            // Select the element that matches the CSS selector
            val element = document.select(cssSelector).firstOrNull()
            val text = element?.text()

            // Log the value found or a warning if the element is not found
            if (text != null) {
                logger.info { "Found the value '$text'" }
            } else {
                logger.warn { "Value not found for CSS selector '$cssSelector'" }
            }

            text
        } catch (exception: Exception) {
            // Log an error message if the URL cannot be accessed
            logger.error { "Failed to access the URL: $url. Exception: $exception" }
            null
        }
    }
}