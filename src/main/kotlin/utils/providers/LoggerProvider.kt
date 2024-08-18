package com.rsdb.utils.providers

import mu.KotlinLogging

/**
 * Provides a singleton instance of a logger using the KotlinLogging library.
 *
 * This object allows for centralized logging configuration and easy access to a logger instance
 * throughout the application.
 *
 * Example usage:
 * ```
 * val logger = LoggerProvider.logger
 * logger.info { "This is an info message." }
 * ```
 */
object LoggerProvider {

    /**
     * Singleton logger instance using KotlinLogging.
     */
    val logger = KotlinLogging.logger {}
}
