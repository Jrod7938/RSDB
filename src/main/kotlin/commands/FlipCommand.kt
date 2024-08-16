package com.gepc.commands

import com.gepc.models.ItemPriceResponse
import com.gepc.utils.HttpClientProvider
import com.gepc.utils.LoggerProvider
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.*
import java.text.NumberFormat
import java.util.*

object FlipCommand {

    private val logger = LoggerProvider.logger

    private const val BUY_THRESHOLD = 0.05
    private const val SELL_THRESHOLD = 0.10

    suspend fun handle(event: ChatInputCommandInteractionCreateEvent) {
        val itemName = event.interaction.command.strings["item"] ?: return
        val itemMessage = executeTradingStrategy(itemName)
        event.interaction.respondPublic { content = itemMessage }
    }

    private suspend fun executeTradingStrategy(itemName: String): String {
        val client = HttpClientProvider.client

        return try {
            logger.info { "Executing trading strategy for: $itemName" }
            val latestPrice = getLatestPrice(client, itemName) ?: return "Item '$itemName' not found."
            val historicalPrices = getHistoricalPrices(client, itemName)

            val sma = calculateSMA(historicalPrices, 30)  // 30-day simple moving average
            val priceVolatility = calculateVolatility(historicalPrices)
            val buySignal = latestPrice.price < sma && priceVolatility < BUY_THRESHOLD
            val sellSignal = latestPrice.price > sma * (1 + SELL_THRESHOLD) && priceVolatility > BUY_THRESHOLD

            val suggestion = when {
                buySignal -> "Consider buying $itemName. Price is below the 30-day SMA and volatility is low."
                sellSignal -> "Consider selling $itemName. Price is above the SMA and volatility is high."
                else -> "No strong buy or sell signal for $itemName at this time."
            }

            logger.info { "Strategy executed for item: $itemName" }
            formatTradeMessage(itemName, latestPrice, sma, priceVolatility, suggestion)
        } catch (e: Exception) {
            logger.error(e) { "Failed to execute trading strategy for '$itemName'" }
            "Failed to execute trading strategy for '$itemName'."
        }
    }

    private suspend fun getLatestPrice(client: HttpClient, itemName: String): ItemPriceResponse? {
        val url = "https://api.weirdgloop.org/exchange/history/rs/latest"
        logger.debug { "Fetching latest price with URL: $url" }

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
                logger.error(e) { "Failed to parse latest price for item: $itemName" }
                null
            }
        }
    }

    private suspend fun getHistoricalPrices(client: HttpClient, itemName: String): List<Int> {
        val url = "https://api.weirdgloop.org/exchange/history/rs/last90d"
        logger.debug { "Fetching historical prices with URL: $url" }

        val response: HttpResponse = client.get(url) {
            parameter("name", itemName)
            parameter("lang", "en")
        }

        val rawResponse = response.bodyAsText()
        logger.debug { "Raw JSON response: $rawResponse" }

        val json = Json.parseToJsonElement(rawResponse).jsonObject
        val matchedKey = json.keys.find { it.equals(itemName, ignoreCase = true) }

        return matchedKey?.let {
            json[matchedKey]?.jsonArray?.map {
                val priceData = it.jsonObject
                priceData["price"]?.jsonPrimitive?.int ?: 0
            } ?: emptyList()
        } ?: emptyList()
    }

    private fun calculateSMA(prices: List<Int>, period: Int): Double {
        if (prices.size < period) return prices.average()
        return prices.takeLast(period).average()
    }

    private fun calculateVolatility(prices: List<Int>): Double {
        val avgPrice = prices.average()
        val squaredDifferences = prices.map { (it - avgPrice) * (it - avgPrice) }
        return Math.sqrt(squaredDifferences.average())
    }

    private fun formatTradeMessage(
        itemName: String,
        latestPrice: ItemPriceResponse,
        sma: Double,
        priceVolatility: Double,
        suggestion: String
    ): String {
        val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(latestPrice.price)
        val formattedSMA = NumberFormat.getNumberInstance(Locale.US).format(sma)

        return """
            ``Trading Strategy Analysis for $itemName:
            - Latest Price: $formattedPrice GP
            - 30-Day SMA: $formattedSMA GP
            - Volatility: ${String.format("%.2f", priceVolatility)}
            - Suggestion: $suggestion
            ``
        """.trimIndent()
    }
}
