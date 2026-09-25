package com.example.model

import ecobudget.shared.generated.resources.Res
import ecobudget.shared.generated.resources.category_alimentation
import ecobudget.shared.generated.resources.category_logement
import ecobudget.shared.generated.resources.category_loisirs
import ecobudget.shared.generated.resources.category_transport
import org.jetbrains.compose.resources.StringResource


/**
 * Représente les catégories obligatoires pour la classification des dépenses dans EcoBudget.
 */
enum class Category(
    val labelResId: StringResource,
    val emoji: String
) {
    TRANSPORT(Res.string.category_transport, "🚌"),
    ALIMENTATION(Res.string.category_alimentation, "🍱"),
    LOISIRS(Res.string.category_loisirs, "🎾"),
    LOGEMENT(Res.string.category_logement, "🏠")
}
