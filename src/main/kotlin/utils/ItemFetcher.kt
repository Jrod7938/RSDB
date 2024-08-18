package com.rsdb.utils

import com.rsdb.utils.providers.LoggerProvider
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class ItemFetcher(private val top100Url: String) {

    private val logger = LoggerProvider.logger

    fun fetchTop100Items(): List<String> {
        logger.info { "Fetching top 100 items from: $top100Url" }
        return try {
            val doc: Document = Jsoup.connect(top100Url).get()
            val rows = doc.select("tr")
            val items = mutableListOf<String>()

            for (row in rows) {
                val itemLink = row.select("td a.table-item-link").firstOrNull()
                val itemName = itemLink?.select("span")?.text()

                if (itemName != null) {
                    items.add(itemName)
                    logger.info { "Found item: $itemName" }
                }
            }

            items
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch top 100 items from $top100Url" }
            emptyList()
        }
    }
}
