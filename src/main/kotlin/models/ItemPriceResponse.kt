package com.rsdb.models

import kotlinx.serialization.Serializable

/**
 * A data class representing the response containing price information for an item in RuneScape.
 *
 * This class includes details such as the item's ID, the timestamp of the price data, the current price,
 * and the trading volume.
 *
 * @property id The unique identifier for the item.
 * @property timestamp The timestamp indicating when the price data was recorded.
 * @property price The current price of the item in coins.
 * @property volume The trading volume of the item (i.e., the number of items traded).
 */
@Serializable
data class ItemPriceResponse(
    val id: String,
    val timestamp: String,
    val price: Int,
    val volume: Int
)
