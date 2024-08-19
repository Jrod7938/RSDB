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
import kotlinx.serialization.json.*

/**
 * A service class that provides methods for fetching the latest and historical prices
 * of items from the RuneScape Grand Exchange.
 *
 * This service interacts with an external API to retrieve price information for a given item.
 *
 * @property client The HTTP client used to make requests. Defaults to [HttpClientProvider.client].
 */
class PriceService(private val client: HttpClient = HttpClientProvider.client) {

    private val logger = LoggerProvider.logger

    /**
     * Fetches the latest price for a given item from the RuneScape Grand Exchange API.
     *
     * This method makes an HTTP GET request to the API and parses the JSON response to extract
     * the latest price information for the specified item.
     *
     * @param itemName The name of the item to fetch the latest price for.
     * @return An [ItemPriceResponse] object containing the latest price details, or `null` if the item is not found.
     */
    suspend fun getLatestPrice(itemName: String): ItemPriceResponse? {
        logger.info { "Fetching latest price for item: $itemName" }
        val url = "https://api.weirdgloop.org/exchange/history/rs/latest"
        val response: HttpResponse = client.get(url) {
            parameter("name", itemName)
            parameter("lang", "en")
        }

        val rawResponse = response.bodyAsText()
        val json = Json.parseToJsonElement(rawResponse).jsonObject
        val matchedKey = json.keys.find { it.equals(itemName, ignoreCase = true) }

        return matchedKey?.let {
            try {
                json[matchedKey]?.let { Json.decodeFromJsonElement<ItemPriceResponse>(it) }
            } catch (e: Exception) {
                logger.error(e) { "Failed to parse latest price for item: $itemName" }
                null
            }
        }
    }

    /**
     * Fetches the historical prices for a given item from the RuneScape Grand Exchange API.
     *
     * This method makes an HTTP GET request to the API and parses the JSON response to extract
     * the price history for the specified item over the last 90 days.
     *
     * @param itemName The name of the item to fetch the historical prices for.
     * @return A list of integers representing the historical prices for the item over the last 90 days, or an empty list if the item is not found.
     */
    suspend fun getHistoricalPrices(itemName: String): List<Int> {
        logger.info { "Fetching historical prices for item: $itemName" }
        val url = "https://api.weirdgloop.org/exchange/history/rs/last90d"
        val response: HttpResponse = client.get(url) {
            parameter("name", itemName)
            parameter("lang", "en")
        }

        val rawResponse = response.bodyAsText()
        val json = Json.parseToJsonElement(rawResponse).jsonObject
        val matchedKey = json.keys.find { it.equals(itemName, ignoreCase = true) }

        return matchedKey?.let {
            json[matchedKey]?.jsonArray?.map {
                val priceData = it.jsonObject
                priceData["price"]?.jsonPrimitive?.int ?: 0
            } ?: emptyList()
        } ?: emptyList()
    }
}
