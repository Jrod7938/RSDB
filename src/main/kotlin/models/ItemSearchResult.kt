package com.gepc.models

import kotlinx.serialization.Serializable

@Serializable
data class ItemSearchResult(
    val id: Int,
    val name: String,
    val icon: String,
    val icon_large: String,
    val type: String,
    val typeIcon: String,
    val description: String,
    val current: PriceInfo,
    val today: TodayPriceInfo,
    val members: String
)

@Serializable
data class TodayPriceInfo(val trend: String, val price: Int)