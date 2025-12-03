package com.milktracker.dailymilk.ui

/**
 * Shared constants used across the UI layer.
 */
object UiConstants {
    /**
     * Regex pattern for validating decimal number input.
     * Matches empty string or valid decimal numbers (e.g., "1", "1.5", ".5").
     */
    val DECIMAL_INPUT_REGEX = Regex("^\\d*\\.?\\d*$")
}
