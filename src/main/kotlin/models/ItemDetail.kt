package com.rsdb.models

import kotlinx.serialization.Serializable

@Serializable
data class ItemDetail(
    val name: String,
    val description: String,
    val current: PriceInfo
)

@Serializable
data class PriceInfo(val trend: String, val price: String)
