package com.gepc.services

import com.gepc.models.ItemPriceResponse
import com.gepc.utils.HttpClientProvider
import com.gepc.utils.LoggerProvider
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import java.text.NumberFormat
import java.util.*

class ItemDetailsService(private val client: HttpClient = HttpClientProvider.client) {

    private val logger = LoggerProvider.logger

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
