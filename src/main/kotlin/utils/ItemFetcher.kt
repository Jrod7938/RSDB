/*
 * This file is part of the RuneScape Discord Bot project.
 *
 * Licensed under the MIT License. You may obtain a copy of the License at
 * https://opensource.org/licenses/MIT
 *
 * © 2024 Jancarlos Rodriguez, Omar Rodriguez
 */

package com.rsdb.utils

import com.rsdb.utils.providers.LoggerProvider
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * A utility class for fetching the top 100 items from a specified URL.
 *
 * This class uses Jsoup to scrape a webpage and extract the names of the top 100 items
 * based on the provided CSS selectors. The fetched items are returned as a list of strings.
 *
 * @property top100Url The URL from which to fetch the top 100 items.
 */
class ItemFetcher(private val top100Url: String) {

    private val logger = LoggerProvider.logger

    /**
     * Fetches the top 100 items from the specified URL.
     *
     * This method connects to the URL, parses the HTML document, and extracts item names from the rows of a table.
     * The items are logged and returned as a list of strings.
     *
     * @return A list of the top 100 item names, or an empty list if the fetch operation fails.
     */
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
