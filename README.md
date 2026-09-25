# EcoBudget 🌿

Dépôt de base pour le projet du cours de développement mobile avancé.

# Document Technique de Synthèse : Migration Multiplateforme (KMP / CMP) — EcoBudget

## 1. Executive Summary

Le projet **EcoBudget** a été entièrement migré d'une architecture mono-plateforme Android vers une architecture **Kotlin Multiplatform (KMP)** avec **Compose Multiplatform (CMP)**. L'ensemble de l'architecture applicative (modèles de données, repositories de gestion d'état/persistance, ViewModel, utilitaires et composants d'interface utilisateur) est désormais centralisé dans le module source commun (`commonMain`). Cette approche garantit une exécution native identique sur **Android** et **iOS**.

---

## 2. Analyse des Fichiers et Couches Migrés vers `commonMain`

### 2.1. Couche Domaine & Modèles (`Transaction.kt`, `Category.kt`, `BudgetMonth.kt`)
* **Le problème rencontré :**  
  Présence d'annotations d'optimisation Android (`@Parcelize`) et implémentation de l'interface `android.os.Parcelable` pour le passage de modèles entre composants Android via la pile de navigation.
* **Le choix technique appliqué :**  
  Suppression complète des dépendances `android.os.*` et ajout du plugin/bibliothèque **kotlinx.serialization** (`@Serializable`).
* **La justification :**  
  `kotlinx.serialization` est la solution multiplateforme officielle JetBrains. Elle permet la sérialisation/désérialisation en JSON ou binaire au niveau de la couche commune sans aucun couplage avec le SDK Android.

---

### 2.2. Couche Accès aux Données (`EcoBudgetRepository.kt` & Implémentations)
* **Le problème rencontré :**  
  Dépendance initiale à des systèmes de stockage Android locaux (`SharedPreferences`, `Room` Android-only ou contextes d'application `android.content.Context`).
* **Le choix technique appliqué :**  
  Architecture basée sur le pattern **Repository** dans `commonMain`, utilisant des flux réactifs Kotlin (`StateFlow` / `SharedFlow`) et de la persistance en mémoire / stockage multiplateforme (ex: **KVault**, **DataStore Multiplatform** ou **SQLDelight**).
* **La justification :**  
  En isolant le contrat d'interface du Repository dans `commonMain`, la logique métier d'accès aux dépenses/budget devient 100 % agnostique du système d'exploitation sous-jacent.

---

### 2.3. Formatage des Devises et Chiffres (`FormatFcfa.kt`)
* **Le problème rencontré :**  
  L'utilisation initiale reposait sur des API spécifiques à la JVM Java (`java.text.NumberFormat` et `java.text.DecimalFormatSymbols`), totalement indisponibles sur iOS (Kotlin/Native).
* **Le choix technique appliqué :**  
  Mise en place d'une fonction d'extension purement algorithmique en Kotlin basée sur les opérations de chaînes de caractères (`chunked(3)`, `reversed()`, `joinToString(" ")`).
* **La justification :**  
  Cette approche s'appuie uniquement sur la bibliothèque standard Kotlin (`kotlin.*`), garantissant une compatibilité 100 % multiplateforme sans recourir au mécanisme `expect/actual`.

---

### 2.4. Gestion des Ressources & Chaînes de Caractères (`strings.xml` / `EcoBudgetScreen.kt`)
* **Le problème rencontré :**  
  Accès aux ressources via `android.content.Context` / `R.string`. Les tentatives initiales d'utilisation de spécificateurs de format dynamique (`%s`, `%d`) dans `stringResource` provoquaient des erreurs de rendu selon la plateforme.
* **Le choix technique appliqué :**  
  Adoption de **Compose Multiplatform Resources** (`org.jetbrains.compose.resources`) pour `Res.string.*`, couplée à l'**interpolation de chaînes native Kotlin** (`"${totalSpent.formatFcfa()} FCFA"`).
* **La justification :**  
  L'interpolation native Kotlin résout les divergences de traitement de `String.format()` entre la JVM et Kotlin/Native, garantissant un affichage stable sur toutes les cibles.

---

### 2.5. Couche Présentation & Architecture Réactive (`EcoBudgetViewModel.kt`, `EcoBudgetUiState.kt`)
* **Le problème rencontré :**  
  Héritage direct de la classe `androidx.lifecycle.ViewModel` d'Android Jetpack, rendant la gestion d'état inexploitable sur iOS.
* **Le choix technique appliqué :**  
  Migration vers la version multiplateforme de **androidx.lifecycle:lifecycle-viewmodel-compose** disponible dans `commonMain`, combinée à `StateFlow` et `CoroutineScope`.
* **La justification :**  
  Maintient l'architecture réactive (*Unidirectional Data Flow*) préconisée par JetBrains et Google, tout en rendant le ViewModel réutilisable sur Android et iOS.

---

### 2.6. Couche Interface Utilisateur (`EcoBudgetScreen.kt`, Dialogs & Components)
* **Le problème rencontré :**  
  Utilisation d'imports déclaratifs liés à Android Jetpack Compose (`androidx.compose.material.*`).
* **Le choix technique appliqué :**  
  Remplacement complet par **Compose Multiplatform Material3** (`org.jetbrains.compose.material3`).
* **La justification :**  
  Grâce au moteur Skia/Skiko (iOS) et Canvas (Android), l'UI est dessinée de manière strictement identique (*pixel-perfect*) sur tous les écrans.

---

## 3. Matrice de Migration des Composants

| Composant / Couche | Dépendance Android initiale | Solution Multiplateforme retenue | Cibles supportées |
| :--- | :--- | :--- | :--- |
| **Model** (`Transaction.kt`) | `android.os.Parcelable` / `@Parcelize` | `@Serializable` (`kotlinx.serialization`) | Android, iOS, Desktop |
| **Repository** (`EcoBudgetRepository.kt`) | `android.content.Context` / `SharedPreferences` | Interface `commonMain` + Coroutines `StateFlow` | Android, iOS, Desktop |
| **Utilitaire** (`FormatFcfa.kt`) | `java.text.NumberFormat` | Algorithme Kotlin Pure (`kotlin.text.*`) | Android, iOS, Desktop |
| **ViewModel / State** (`EcoBudgetViewModel.kt`) | `androidx.lifecycle.ViewModel` (Android) | Jetpack Lifecycle Multiplatform (`commonMain`) | Android, iOS |
| **Ressources UI** | `R.string` / `res/values` | `org.jetbrains.compose.resources` (`Res.string`) | Android, iOS, Desktop |
| **Vues / Composants UI** | `androidx.compose.material.*` (Android) | `org.jetbrains.compose.material3` (CMP) | Android, iOS, Desktop |