/*
 * This file is part of the RuneScape Discord Bot project.
 *
 * Licensed under the MIT License. You may obtain a copy of the License at
 * https://opensource.org/licenses/MIT
 *
 * © 2024 Jancarlos Rodriguez, Omar Rodriguez
 */

package com.rsdb.services

import com.rsdb.models.ItemPriceResponse
import com.rsdb.utils.providers.HttpClientProvider
import com.rsdb.utils.providers.LoggerProvider
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import java.text.NumberFormat
import java.util.*

/**
 * A service class that fetches and formats item details from the RuneScape Grand Exchange.
 *
 * This service interacts with an external API to retrieve the latest price and volume information
 * for a given item and formats the data into a user-friendly message.
 *
 * @property client The HTTP client used to make requests. Defaults to [HttpClientProvider.client].
 */
class ItemDetailsService(private val client: HttpClient = HttpClientProvider.client) {

    private val logger = LoggerProvider.logger

    /**
     * Fetches item details for a given item name and returns a formatted message.
     *
     * This method first attempts to retrieve the item price data from an external API.
     * If successful, it formats the data into a message; otherwise, it returns an error message.
     *
     * @param itemName The name of the item to fetch details for.
     * @return A formatted string containing item details or an error message.
     */
    suspend fun getItemDetailsMessage(itemName: String): String {
        return try {
            logger.info { "Fetching item details for: $itemName" }
            val itemPriceResponse = fetchItemPrice(itemName) ?: return "Item '$itemName' not found."

            logger.info { "Successfully fetched details for item: $itemName" }
            formatItemMessage(itemName, itemPriceResponse)
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch details for '$itemName'" }
            "Failed to fetch details for '$itemName'."
        }
    }

    /**
     * Fetches the price details for a given item name from the RuneScape Grand Exchange API.
     *
     * This method makes an HTTP GET request to the API and parses the JSON response
     * to extract price information for the specified item.
     *
     * @param itemName The name of the item to fetch the price for.
     * @return An [ItemPriceResponse] object containing the item's price details, or `null` if the item is not found.
     */
    private suspend fun fetchItemPrice(itemName: String): ItemPriceResponse? {
        val url = "https://api.weirdgloop.org/exchange/history/rs/latest"
        logger.debug { "Searching for item price with URL: $url" }

        val response: HttpResponse = client.get(url) {
            parameter("name", itemName)
            parameter("lang", "en")
        }

        val rawResponse = response.bodyAsText()
        logger.debug { "Raw JSON response: $rawResponse" }

        val json = Json.parseToJsonElement(rawResponse).jsonObject
        val matchedKey = json.keys.find { it.equals(itemName, ignoreCase = true) }

        return matchedKey?.let {
            try {
                json[matchedKey]?.let { Json.decodeFromJsonElement<ItemPriceResponse>(it) }
            } catch (e: Exception) {
                logger.error(e) { "Failed to parse response for item: $itemName" }
                null
            }
        } ?: run {
            logger.warn { "No matching key found for item: $itemName" }
            null
        }
    }

    /**
     * Formats the item details into a message string.
     *
     * The message includes the item's ID, price, volume, and timestamp, formatted for readability.
     *
     * @param itemName The name of the item.
     * @param itemPriceResponse The [ItemPriceResponse] containing the item's details.
     * @return A formatted string with the item's details.
     */
    private fun formatItemMessage(itemName: String, itemPriceResponse: ItemPriceResponse): String {
        val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(itemPriceResponse.price)
        val formattedVolume = NumberFormat.getNumberInstance(Locale.US).format(itemPriceResponse.volume)

        return """
            ```kotlin
            $itemName
            ID: ${itemPriceResponse.id}
            Price: $formattedPrice GP
            Volume: $formattedVolume
            Timestamp: ${itemPriceResponse.timestamp}
            ```
        """.trimIndent()
    }
}
