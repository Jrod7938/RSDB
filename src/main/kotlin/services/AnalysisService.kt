package com.gepc.services

import com.gepc.utils.providers.LoggerProvider

class AnalysisService {

    private val logger = LoggerProvider.logger

    fun calculateSMA(prices: List<Int>, period: Int): Double {
        logger.info { "Calculating SMA with period: $period" }
        return if (prices.size < period) prices.average() else prices.takeLast(period).average()
    }

    fun calculateVolatility(prices: List<Int>): Double {
        logger.info { "Calculating price volatility" }
        val avgPrice = prices.average()
        if (avgPrice == 0.0) return 0.0 // Avoid division by zero if the average price is zero

        val squaredDifferences = prices.map { (it - avgPrice) * (it - avgPrice) }
        val variance = squaredDifferences.average()

        return Math.sqrt(variance) / avgPrice // Relative volatility (coefficient of variation)
    }

    fun calculateMargin(sma: Double, latestPrice: Double): Double {
        logger.info { "Calculating margin with SMA: $sma and latest price: $latestPrice" }
        return sma - latestPrice
    }

    fun shouldBuy(latestPrice: Double, sma: Double, margin: Double, priceVolatility: Double, buyThreshold: Double): Boolean {
        logger.info { "Determining if should buy with latest price: $latestPrice, SMA: $sma, margin: $margin, volatility: $priceVolatility" }
        return latestPrice < sma && margin > 0 && priceVolatility < buyThreshold
    }
}
