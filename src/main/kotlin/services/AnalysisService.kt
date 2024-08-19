/*
 * This file is part of the RuneScape Discord Bot project.
 *
 * Licensed under the MIT License. You may obtain a copy of the License at
 * https://opensource.org/licenses/MIT
 *
 * © 2024 Jancarlos Rodriguez, Omar Rodriguez
 */

package com.rsdb.services

import com.rsdb.utils.providers.LoggerProvider

/**
 * A service class that provides various analysis methods for financial data.
 *
 * This class includes methods to calculate the Simple Moving Average (SMA), price volatility,
 * margin, and whether to buy based on certain criteria.
 */
class AnalysisService {

    private val logger = LoggerProvider.logger

    /**
     * Calculates the Simple Moving Average (SMA) for a list of prices over a specified period.
     *
     * @param prices The list of prices to analyze.
     * @param period The number of periods over which to calculate the SMA.
     * @return The calculated SMA, or the average of all prices if the period exceeds the list size.
     */
    fun calculateSMA(prices: List<Int>, period: Int): Double {
        logger.info { "Calculating SMA with period: $period" }
        return if (prices.size < period) prices.average() else prices.takeLast(period).average()
    }

    /**
     * Calculates the price volatility for a list of prices.
     *
     * Volatility is calculated as the coefficient of variation, which is the ratio of the
     * standard deviation to the average price.
     *
     * @param prices The list of prices to analyze.
     * @return The calculated price volatility as a relative value, or 0.0 if the average price is zero.
     */
    fun calculateVolatility(prices: List<Int>): Double {
        logger.info { "Calculating price volatility" }
        val avgPrice = prices.average()
        if (avgPrice == 0.0) return 0.0 // Avoid division by zero if the average price is zero

        val squaredDifferences = prices.map { (it - avgPrice) * (it - avgPrice) }
        val variance = squaredDifferences.average()

        return Math.sqrt(variance) / avgPrice // Relative volatility (coefficient of variation)
    }

    /**
     * Calculates the margin between the Simple Moving Average (SMA) and the latest price.
     *
     * @param sma The Simple Moving Average (SMA) value.
     * @param latestPrice The most recent price.
     * @return The margin, calculated as SMA minus the latest price.
     */
    fun calculateMargin(sma: Double, latestPrice: Double): Double {
        logger.info { "Calculating margin with SMA: $sma and latest price: $latestPrice" }
        return sma - latestPrice
    }

    /**
     * Determines whether to buy based on the latest price, SMA, margin, price volatility, and a buy threshold.
     *
     * The decision is based on the criteria that the latest price is below the SMA, the margin is positive,
     * and the price volatility is below the specified threshold.
     *
     * @param latestPrice The most recent price.
     * @param sma The Simple Moving Average (SMA) value.
     * @param margin The margin calculated from SMA and latest price.
     * @param priceVolatility The calculated price volatility.
     * @param buyThreshold The volatility threshold below which buying is considered.
     * @return `true` if the conditions are met for buying, otherwise `false`.
     */
    fun shouldBuy(latestPrice: Double, sma: Double, margin: Double, priceVolatility: Double, buyThreshold: Double): Boolean {
        logger.info { "Determining if should buy with latest price: $latestPrice, SMA: $sma, margin: $margin, volatility: $priceVolatility" }
        return latestPrice < sma && margin > 0 && priceVolatility < buyThreshold
    }
}
