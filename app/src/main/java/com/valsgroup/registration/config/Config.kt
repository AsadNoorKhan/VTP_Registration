package com.valsgroup.registration.config

import android.content.Context
import java.io.IOException
import java.util.Properties

/**
 * Configuration object that reads values from config.properties file
 * Provides centralized access to all configuration values
 */
object Config {
    
    private var properties: Properties? = null
    
    // API Configuration
    const val DEFAULT_API_BASE_URL = "https://api.valsgroup.com"
    const val DEFAULT_API_VERSION = "v1"
    const val DEFAULT_API_TIMEOUT = 30000L
    

    
    // API Endpoints
    const val DEFAULT_ENDPOINT_USER_STATUS = "/vtp/userstatus"
    const val DEFAULT_ENDPOINT_USER_REGISTER = "/vtp/imeireg"
    
    // Network Configuration
    const val DEFAULT_NETWORK_TIMEOUT = 30
    const val DEFAULT_RETRY_ATTEMPTS = 3
    
    // App Configuration
    const val DEFAULT_APP_VERSION = "1.0.0"
    const val DEFAULT_BUILD_TYPE = "debug"
    
    /**
     * Initialize configuration by reading from properties file
     * @param context Application context
     */
    fun init(context: Context) {
        try {
            properties = Properties()
            context.assets.open("config.properties").use { inputStream ->
                properties?.load(inputStream)
            }
        } catch (e: IOException) {
            // If properties file can't be read, use default values
            properties = null
        }
    }
    
    /**
     * Get configuration value with fallback to default
     */
    private fun getConfigValue(key: String, defaultValue: String): String {
        return properties?.getProperty(key) ?: defaultValue
    }
    
    private fun getConfigValue(key: String, defaultValue: Int): Int {
        return properties?.getProperty(key)?.toIntOrNull() ?: defaultValue
    }
    
    private fun getConfigValue(key: String, defaultValue: Long): Long {
        return properties?.getProperty(key)?.toLongOrNull() ?: defaultValue
    }
    
    // API Configuration getters
    val apiBaseUrl: String
        get() = getConfigValue("API_BASE_URL", DEFAULT_API_BASE_URL)
    
    val apiVersion: String
        get() = getConfigValue("API_VERSION", DEFAULT_API_VERSION)
    
    val apiTimeout: Long
        get() = getConfigValue("API_TIMEOUT", DEFAULT_API_TIMEOUT)
    

    
    // API Endpoints getters
    val endpointUserStatus: String
        get() = getConfigValue("ENDPOINT_USER_STATUS", DEFAULT_ENDPOINT_USER_STATUS)
    
    val endpointUserRegister: String
        get() = getConfigValue("ENDPOINT_USER_REGISTER", DEFAULT_ENDPOINT_USER_REGISTER)
    

    
    // Network Configuration getters
    val networkTimeout: Int
        get() = getConfigValue("NETWORK_TIMEOUT", DEFAULT_NETWORK_TIMEOUT)
    
    val retryAttempts: Int
        get() = getConfigValue("RETRY_ATTEMPTS", DEFAULT_RETRY_ATTEMPTS)
    
    // App Configuration getters
    val appVersion: String
        get() = getConfigValue("APP_VERSION", DEFAULT_APP_VERSION)
    
    val buildType: String
        get() = getConfigValue("BUILD_TYPE", DEFAULT_BUILD_TYPE)
    
    /**
     * Get full API URL by combining base URL and endpoint
     */
    fun getFullApiUrl(endpoint: String): String {
        return "$apiBaseUrl$endpoint"
    }
    
    /**
     * Get full API URL with version
     */
    fun getFullApiUrlWithVersion(endpoint: String): String {
        return "$apiBaseUrl/$apiVersion$endpoint"
    }
}
