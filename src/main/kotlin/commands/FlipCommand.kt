/*
 * This file is part of the RuneScape Discord Bot project.
 *
 * Licensed under the MIT License. You may obtain a copy of the License at
 * https://opensource.org/licenses/MIT
 *
 * © 2024 Jancarlos Rodriguez, Omar Rodriguez
 */

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

/**
 * Command handler for the "flip" command in the RuneScape Discord bot.
 *
 * This command analyzes items from the RuneScape Grand Exchange to find the best flipping opportunities.
 * It calculates metrics such as the Simple Moving Average (SMA), price volatility, and margin to suggest
 * whether an item is a good buy candidate.
 */
object FlipCommand {

    private val logger = LoggerProvider.logger
    private const val BUY_THRESHOLD = 0.05
    private const val TOP_100_URL = "https://secure.runescape.com/m=itemdb_rs/top100?list=1"

    private val priceService = PriceService()
    private val analysisService = AnalysisService()
    private val itemFetcher = ItemFetcher(TOP_100_URL)

    /**
     * Handles the execution of the "flip" command.
     *
     * Depending on the provided item name, this method either finds the best item to flip from the top 100 items
     * or analyzes a specific item to determine if it's a good buy candidate.
     *
     * @param event The event triggered by the "flip" command.
     */
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

    /**
     * Finds the best item to flip from the top 100 items on the RuneScape Grand Exchange.
     *
     * This method analyzes each item in the top 100 list to determine the best buy and overall flip candidate
     * based on margin and price volatility.
     *
     * @return A message with the best flipping opportunities.
     */
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

    /**
     * Builds the message to display the best flip opportunities.
     *
     * @param bestBuyItem The name of the item with the best buy margin.
     * @param bestBuyMargin The best buy margin found.
     * @param bestOverallItem The name of the item with the best overall margin.
     * @param bestOverallMargin The best overall margin found.
     * @return A message summarizing the best flip opportunities.
     */
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

    /**
     * Executes the buy strategy for a specific item.
     *
     * This method analyzes the item based on its latest price, SMA, volatility, and margin to determine
     * if it's a good buy candidate.
     *
     * @param itemName The name of the item to analyze.
     * @return A message with the analysis results and suggestions.
     */
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

    /**
     * Formats the trade analysis message for a specific item.
     *
     * This message includes the latest price, 30-day SMA, margin, volatility, and a suggestion
     * based on the analysis results.
     *
     * @param itemName The name of the item being analyzed.
     * @param latestPrice The latest price information for the item.
     * @param sma The 30-day Simple Moving Average (SMA) for the item.
     * @param priceVolatility The calculated price volatility for the item.
     * @param margin The margin between the SMA and the latest price.
     * @param suggestion The suggested action based on the analysis.
     * @return A formatted string with the trade analysis results.
     */
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
