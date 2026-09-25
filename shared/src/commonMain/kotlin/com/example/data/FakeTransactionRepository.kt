package com.example.data

import com.example.model.Category
import com.example.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Implémentation factice (Mock/In-Memory) de [TransactionRepository] pour simuler l'accès
 * aux données sans base de données réelle.
 * Compatible Kotlin Multiplatform (KMP) - Aucune dépendance java.util.*
 */
class FakeTransactionRepository : TransactionRepository {

    private val _transactionsFlow: MutableStateFlow<List<Transaction>>

    init {
        // Base de temps fixe (Septembre 2026) pour un comportement déterministe en KMP
        val baseTimestamp = 1758820000000L // Approx Septembre 2026
        val oneMonthMs = 30L * 24 * 60 * 60 * 1000L

        fun getTimeForMonth(monthOffset: Int, day: Int, hour: Int): Long {
            val dayOffsetMs = (day - 1) * 24L * 60 * 60 * 1000L
            val hourOffsetMs = hour * 60L * 60 * 1000L
            return baseTimestamp + (monthOffset * oneMonthMs) + dayOffsetMs + hourOffsetMs
        }

        var idCounter = 100

        val initialList = listOf(
            // Mois actuel (0)
            Transaction(
                id = "tx_${idCounter++}",
                title = "Supermarché Bio",
                amount = 45000.0,
                date = getTimeForMonth(0, 22, 14),
                category = Category.ALIMENTATION
            ),
            Transaction(
                id = "tx_${idCounter++}",
                title = "Session Tennis",
                amount = 12000.0,
                date = getTimeForMonth(0, 20, 10),
                category = Category.LOISIRS
            ),
            Transaction(
                id = "tx_${idCounter++}",
                title = "Ticket de Bus Express",
                amount = 2500.0,
                date = getTimeForMonth(0, 18, 8),
                category = Category.TRANSPORT
            ),
            Transaction(
                id = "tx_${idCounter++}",
                title = "Loyer Mensuel",
                amount = 250000.0,
                date = getTimeForMonth(0, 5, 9),
                category = Category.LOGEMENT
            ),
            Transaction(
                id = "tx_${idCounter++}",
                title = "Boulangerie & Pâtisserie",
                amount = 4800.0,
                date = getTimeForMonth(0, 15, 16),
                category = Category.ALIMENTATION
            ),
            Transaction(
                id = "tx_${idCounter++}",
                title = "Recharge Vélo Électrique",
                amount = 3500.0,
                date = getTimeForMonth(0, 12, 11),
                category = Category.TRANSPORT
            ),
            Transaction(
                id = "tx_${idCounter++}",
                title = "Facture Électricité",
                amount = 48000.0,
                date = getTimeForMonth(0, 8, 15),
                category = Category.LOGEMENT
            ),

            // Mois précédent (-1)
            Transaction(
                id = "tx_${idCounter++}",
                title = "Loyer Mois Précédent",
                amount = 250000.0,
                date = getTimeForMonth(-1, 5, 9),
                category = Category.LOGEMENT
            ),
            Transaction(
                id = "tx_${idCounter++}",
                title = "Courses du mois",
                amount = 65000.0,
                date = getTimeForMonth(-1, 10, 15),
                category = Category.ALIMENTATION
            ),
            Transaction(
                id = "tx_${idCounter++}",
                title = "Abonnement Transport",
                amount = 35000.0,
                date = getTimeForMonth(-1, 2, 8),
                category = Category.TRANSPORT
            ),
            Transaction(
                id = "tx_${idCounter++}",
                title = "Sortie Restaurant",
                amount = 22000.0,
                date = getTimeForMonth(-1, 20, 20),
                category = Category.LOISIRS
            ),

            // Mois suivant (+1)
            Transaction(
                id = "tx_${idCounter++}",
                title = "Avance Loyer Prévue",
                amount = 250000.0,
                date = getTimeForMonth(1, 1, 9),
                category = Category.LOGEMENT
            ),
            Transaction(
                id = "tx_${idCounter++}",
                title = "Abonnement Salle de Sport",
                amount = 20000.0,
                date = getTimeForMonth(1, 3, 10),
                category = Category.LOISIRS
            )
        )

        _transactionsFlow = MutableStateFlow(initialList)
    }

    /**
     * Expose la liste des transactions sous forme de flux réactif asynchrone [Flow].
     */
    override fun getTransactions(): Flow<List<Transaction>> {
        return _transactionsFlow.asStateFlow()
    }

    /**
     * Enregistre une nouvelle dépense dans le flux réactif.
     */
    override suspend fun addTransaction(transaction: Transaction) {
        _transactionsFlow.update { currentList ->
            listOf(transaction) + currentList
        }
    }

    /**
     * Met à jour une dépense existante dans le flux réactif.
     */
    override suspend fun updateTransaction(transaction: Transaction) {
        _transactionsFlow.update { currentList ->
            currentList.map { if (it.id == transaction.id) transaction else it }
        }
    }

    /**
     * Supprime une dépense par son identifiant unique dans le flux réactif.
     */
    override suspend fun deleteTransaction(id: String) {
        _transactionsFlow.update { currentList ->
            currentList.filterNot { it.id == id }
        }
    }
}