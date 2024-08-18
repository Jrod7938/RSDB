package com.rsdb.models

import kotlinx.serialization.Serializable

@Serializable
data class ItemPriceResponse(
    val id: String,
    val timestamp : String,
    val price: Int,
    val volume: Int
)
