package com.rsdb.utils.providers

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Provides a singleton instance of an [HttpClient] configured with the CIO engine
 * and JSON serialization settings.
 *
 * This object uses lazy initialization to ensure the client is only created when needed.
 * The client is configured with the following JSON settings:
 * - `ignoreUnknownKeys`: Unknown JSON keys are ignored during deserialization.
 * - `isLenient`: Allows lenient parsing of JSON.
 * - `prettyPrint`: Pretty prints the JSON output.
 *
 * Example usage:
 * ```
 * val client = HttpClientProvider.client
 * ```
 */
object HttpClientProvider {

    /**
     * Singleton [HttpClient] instance configured with the CIO engine and
     * JSON serialization settings.
     */
    val client: HttpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                })
            }
        }
    }
}
