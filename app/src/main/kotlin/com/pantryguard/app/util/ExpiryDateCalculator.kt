package com.pantryguard.app.util

object ExpiryDateCalculator {
    /**
     * Returns a suggested expiration timestamp (epoch ms) based on the product category.
     * Dairy products get +7 days, Meat gets +4 days, and all other categories get +14 days.
     */
    fun calculateExpiry(category: String?): Long {
        val daysToAdd = when {
            category?.contains("dairy", ignoreCase = true) == true -> 7L
            category?.contains("meat", ignoreCase = true) == true -> 4L
            else -> 14L
        }
        val now = System.currentTimeMillis()
        val millisPerDay = 24L * 60 * 60 * 1000
        return now + daysToAdd * millisPerDay
    }
}
