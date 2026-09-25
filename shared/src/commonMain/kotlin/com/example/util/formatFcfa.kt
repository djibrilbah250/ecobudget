package com.example.util

/**
 * Formate un montant Double en représentation textuelle avec séparateurs de milliers.
 * Compatible Kotlin Multiplatform (iOS, Android, Desktop).
 *
 * Exemple : 500000.0 -> "500 000"
 */
fun Double.formatFcfa(): String {
    val longValue = this.toLong()
    val text = longValue.toString()

    // Si la valeur est négative, on conserve le signe
    val isNegative = text.startsWith("-")
    val cleanText = if (isNegative) text.substring(1) else text

    val formatted = cleanText
        .reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()

    return if (isNegative) "-$formatted" else formatted
}

fun Long.formatFcfa(): String = this.toDouble().formatFcfa()
fun Int.formatFcfa(): String = this.toDouble().formatFcfa()