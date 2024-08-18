package com.rsdb.commands

import com.rsdb.models.ItemPriceResponse
import com.rsdb.services.AnalysisService
import com.rsdb.services.PriceService
import com.rsdb.utils.ItemFetcher
import com.rsdb.utils.providers.LoggerProvider
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import java.text.NumberFormat
import java.util.*

object FlipCommand {

    private val logger = LoggerProvider.logger
    private const val BUY_THRESHOLD = 0.05
    private const val TOP_100_URL = "https://secure.runescape.com/m=itemdb_rs/top100?list=1"

    private val priceService = PriceService()
    private val analysisService = AnalysisService()
    private val itemFetcher = ItemFetcher(TOP_100_URL)

    suspend fun handle(event: ChatInputCommandInteractionCreateEvent) {
        logger.info { "Handling command: ${event.interaction.command.rootName}" }
        val deferredResponse = event.interaction.deferPublicResponse()

        val itemName = event.interaction.command.strings["item"]
        val itemMessage = if (itemName.isNullOrBlank()) {
            findBestFlip()
        } else {
            executeBuyStrategy(itemName)
        }

        deferredResponse.respond { content = itemMessage }
        logger.info { "Response sent to Discord" }
    }

    private suspend fun findBestFlip(): String {
        logger.info { "Fetching top 100 items for flipping analysis" }
        val itemList = itemFetcher.fetchTop100Items()

        if (itemList.isEmpty()) {
            logger.warn { "No items found in top 100 list" }
            return "No suitable item found for flipping at this time."
        }

        var bestBuyItem: String? = null
        var bestBuyMargin = Double.POSITIVE_INFINITY
        var bestOverallItem: String? = null
        var bestOverallMargin = Double.NEGATIVE_INFINITY

        for (item in itemList) {
            logger.info { "Analyzing item: $item" }

            val latestPrice = priceService.getLatestPrice(item)
            if (latestPrice == null) {
                logger.warn { "No latest price found for item: $item" }
                continue
            }

            val historicalPrices = priceService.getHistoricalPrices(item)

            val sma = analysisService.calculateSMA(historicalPrices, 30)
            val priceVolatility = analysisService.calculateVolatility(historicalPrices)
            val margin = analysisService.calculateMargin(sma, latestPrice.price.toDouble())

            logger.debug {
                "Item: $item, Latest Price: ${latestPrice.price}, SMA: $sma, " +
                        "Volatility: $priceVolatility, Margin: $margin"
            }

            if (analysisService.shouldBuy(latestPrice.price.toDouble(), sma, margin, priceVolatility, BUY_THRESHOLD)) {
                bestBuyMargin = margin
                bestBuyItem = item
                logger.info { "$item is the current best buy candidate with a margin of $margin" }
            }

            if (margin > bestOverallMargin) {
                bestOverallMargin = margin
                bestOverallItem = item
            }
        }

        return buildBestFlipMessage(bestBuyItem, bestBuyMargin, bestOverallItem, bestOverallMargin)
    }

    private suspend fun buildBestFlipMessage(
        bestBuyItem: String?,
        bestBuyMargin: Double,
        bestOverallItem: String?,
        bestOverallMargin: Double
    ): String {
        logger.info { "Building flip message" }
        return when {
            bestBuyItem != null -> {
                logger.info { "Best buy item selected: $bestBuyItem" }
                executeBuyStrategy(bestBuyItem)
            }
            bestOverallItem != null -> {
                logger.info { "Best overall item selected: $bestOverallItem" }
                executeBuyStrategy(bestOverallItem)
            }
            else -> {
                logger.warn { "No suitable item found for flipping" }
                "No suitable item found for flipping at this time."
            }
        }
    }

    private suspend fun executeBuyStrategy(itemName: String): String {
        logger.info { "Executing buy strategy for item: $itemName" }
        val latestPrice = priceService.getLatestPrice(itemName) ?: run {
            logger.warn { "No latest price found for item: $itemName" }
            return "Item '$itemName' not found."
        }
        val historicalPrices = priceService.getHistoricalPrices(itemName)

        val sma = analysisService.calculateSMA(historicalPrices, 30)
        val priceVolatility = analysisService.calculateVolatility(historicalPrices)
        val margin = analysisService.calculateMargin(sma, latestPrice.price.toDouble())

        logger.info { "Calculated SMA: $sma, Volatility: $priceVolatility, Margin: $margin" }

        val suggestion = if (margin > 0 && priceVolatility < BUY_THRESHOLD && latestPrice.price.toDouble() < sma) {
            "Consider buying $itemName. Price is below the 30-day SMA and volatility is low."
        } else {
            "Not a good time to buy $itemName."
        }

        return formatTradeMessage(itemName, latestPrice, sma, priceVolatility, margin, suggestion)
    }

    private fun formatTradeMessage(
        itemName: String,
        latestPrice: ItemPriceResponse,
        sma: Double,
        priceVolatility: Double,
        margin: Double,
        suggestion: String
    ): String {
        logger.info { "Formatting trade message for item: $itemName" }
        val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(latestPrice.price)
        val formattedSMA = NumberFormat.getNumberInstance(Locale.US).format(sma)
        val formattedMargin = NumberFormat.getNumberInstance(Locale.US).format(margin)

        return """
            ```kotlin
            Trading Strategy Analysis for $itemName:
            - Latest Price: $formattedPrice GP
            - 30-Day SMA: $formattedSMA GP
            - Margin: $formattedMargin GP
            - Volatility: ${String.format("%.2f", priceVolatility)}
            - Suggestion: $suggestion
            ```
        """.trimIndent()
    }
}
