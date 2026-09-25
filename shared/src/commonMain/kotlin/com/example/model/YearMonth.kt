package com.example.model

/**
 * Modèle immuable représentant un mois spécifique pour la navigation budgétaire.
 * Compatible Kotlin Multiplatform (KMP) - Aucune dépendance java.util.*
 *
 * @property year Année (ex: 2026).
 * @property month Index du mois de 0 (Janvier) à 11 (Décembre).
 */
data class YearMonth(
    val year: Int,
    val month: Int
) {
    /**
     * Libellé formaté en français (ex: "Août 2026").
     */
    val displayLabel: String
        get() {
            val monthName = MONTH_NAMES.getOrNull(month) ?: ""
            return "$monthName $year"
        }

    /**
     * Retourne le YearMonth précédent.
     */
    fun previous(): YearMonth {
        return if (month == 0) {
            YearMonth(year - 1, 11)
        } else {
            YearMonth(year, month - 1)
        }
    }

    /**
     * Retourne le YearMonth suivant.
     */
    fun next(): YearMonth {
        return if (month == 11) {
            YearMonth(year + 1, 0)
        } else {
            YearMonth(year, month + 1)
        }
    }

    /**
     * Vérifie si un timestamp millisecondes appartient à ce mois précis.
     */
    fun containsTimestamp(timestamp: Long): Boolean {
        val target = fromTimestamp(timestamp)
        return target.year == year && target.month == month
    }

    companion object {
        private val MONTH_NAMES = listOf(
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        )

        // Nombre de jours cumulés avant chaque mois (pour année non bissextile)
        private val DAYS_BEFORE_MONTH = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)

        private fun isLeapYear(year: Int): Boolean {
            return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
        }

        /**
         * Convertit un timestamp UTC (millisecondes) en YearMonth.
         */
        fun fromTimestamp(timestamp: Long): YearMonth {
            var days = (timestamp / 86_400_000L).toInt()

            var year = 1970
            while (true) {
                val daysInYear = if (isLeapYear(year)) 366 else 365
                if (days < daysInYear) break
                days -= daysInYear
                year++
            }

            val isLeap = isLeapYear(year)
            var month = 0
            for (m in 11 downTo 0) {
                var monthStartDay = DAYS_BEFORE_MONTH[m]
                if (m > 1 && isLeap) monthStartDay += 1
                if (days >= monthStartDay) {
                    month = m
                    break
                }
            }

            return YearMonth(year, month)
        }

        /**
         * Crée le YearMonth courant à partir d'un timestamp.
         */
        fun current(currentTimeMillis: Long = 1758820000000L): YearMonth {
            return fromTimestamp(currentTimeMillis)
        }
    }
}