package com.rsdb.services

import com.rsdb.models.ItemPriceResponse
import com.rsdb.utils.providers.HttpClientProvider
import com.rsdb.utils.providers.LoggerProvider
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.*

class PriceService(private val client: HttpClient = HttpClientProvider.client) {

    private val logger = LoggerProvider.logger

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
