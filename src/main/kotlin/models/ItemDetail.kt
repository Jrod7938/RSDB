/*
 * This file is part of the RuneScape Discord Bot project.
 *
 * Licensed under the MIT License. You may obtain a copy of the License at
 * https://opensource.org/licenses/MIT
 *
 * © 2024 Jancarlos Rodriguez, Omar Rodriguez
 */

package com.rsdb.models

import kotlinx.serialization.Serializable

/**
 * A data class representing detailed information about an item in RuneScape.
 *
 * This class includes the item's name, description, and current price information.
 *
 * @property name The name of the item.
 * @property description A brief description of the item.
 * @property current The current price information for the item, represented by [PriceInfo].
 */
@Serializable
data class ItemDetail(
    val name: String,
    val description: String,
    val current: PriceInfo
)

/**
 * A data class representing the price information of an item in RuneScape.
 *
 * This class includes the current price and the trend of the price (e.g., rising, falling).
 *
 * @property trend The trend of the item's price (e.g., "rising", "falling").
 * @property price The current price of the item as a string.
 */
@Serializable
data class PriceInfo(
    val trend: String,
    val price: String
)
