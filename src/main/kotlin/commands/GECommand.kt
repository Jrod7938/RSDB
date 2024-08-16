package com.gepc.commands

import com.gepc.models.ItemPriceResponse
import com.gepc.utils.HttpClientProvider
import com.gepc.utils.LoggerProvider
import dev.kord.core.entity.Message
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import java.text.NumberFormat
import java.util.*

object GECommand {

    private val logger = LoggerProvider.logger

    suspend fun handle(message: Message) {
        val itemName = message.content.removePrefix("/ge").trim()
        if (itemName.isEmpty()) {
            logger.warn { "No item name provided in the /ge command." }
            message.channel.createMessage("Please provide an item name.")
            return
        }

        val itemMessage = fetchItemDetails(itemName)
        message.channel.createMessage(itemMessage)
    }

    private suspend fun fetchItemDetails(itemName: String): String {
        val client = HttpClientProvider.client

        return try {
            logger.info { "Fetching item details for: $itemName" }
            val itemPriceResponse = searchItemPrice(client, itemName) ?: return "Item '$itemName' not found."

            logger.info { "Successfully fetched details for item: $itemName" }
            formatItemMessage(itemName, itemPriceResponse)
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch details for '$itemName'" }
            "Failed to fetch details for '$itemName'."
        }
    }

    private suspend fun searchItemPrice(client: HttpClient, itemName: String): ItemPriceResponse? {
        val url = "https://api.weirdgloop.org/exchange/history/rs/latest"
        logger.debug { "Searching for item price with URL: $url" }

        val response: HttpResponse = client.get(url) {
            parameter("name", itemName)
            parameter("lang", "en")
        }

        val rawResponse = response.bodyAsText()
        logger.debug { "Raw JSON response: $rawResponse" }

        val json = Json.parseToJsonElement(rawResponse).jsonObject

        // Log available keys to diagnose the issue
        val availableKeys = json.keys.joinToString(", ")
        logger.debug { "Available keys in the JSON response: $availableKeys" }

        // Attempt case-insensitive match for the item name
        val matchedKey = json.keys.find { it.equals(itemName, ignoreCase = true) }

        return if (matchedKey != null) {
            try {
                json[matchedKey]?.let { Json.decodeFromJsonElement<ItemPriceResponse>(it) }
            } catch (e: Exception) {
                logger.error(e) { "Failed to parse response for item: $itemName" }
                null
            }
        } else {
            logger.warn { "No matching key found for item: $itemName" }
            null
        }
    }

    private fun formatItemMessage(itemName: String, itemPriceResponse: ItemPriceResponse): String {
        val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(itemPriceResponse.price)
        val formattedVolume = NumberFormat.getNumberInstance(Locale.US).format(itemPriceResponse.volume)

        return """
            ``$itemName
            ID: ${itemPriceResponse.id}
            Price: $formattedPrice GP
            Volume: $formattedVolume
            Timestamp: ${itemPriceResponse.timestamp}
            ``
        """.trimIndent()
    }
}
