package com.example.culinar.viewmodels


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.culinar.models.Recipe


enum class Filter { ALL, DAILY, SEARCH, FAVORITES, HISTORY }

class RecipeViewModel : ViewModel() {
    private var nextId = 1
    var recipes = mutableStateListOf<Recipe>()
        private set

    private val _filter = mutableStateOf(Filter.ALL)
    val filter: Filter
        get() = _filter.value  // Permet de lire la valeur du filtre


    var history = mutableStateListOf<Recipe>()
        private set
    // Mot de recherche pour la liste des recettes
    var searchRecipes by mutableStateOf("")
        private set

    // Mettre à jour le mot de recherche en fonction de la saisie de l'utilisateur
    fun UpdateSearchRecipes(query: String) {
        searchRecipes = query
    }

    init {
        // Exemples de recettes
        recipes.addAll(listOf(
            Recipe("1Salade César", "url_image1", "15 min", "Facile",
                ingredients = listOf("Laitue", "Poulet", "Parmesan"), steps = listOf("Couper la laitue", "Griller le poulet")),
            Recipe("2Tarte aux pommes", "url_image2", "45 min", "Moyen",
                ingredients = listOf("Pommes", "Pâte", "Sucre"), steps = listOf("Peler les pommes", "Monter la tarte")),
            Recipe("3Salade César", "url_image1", "15 min", "Facile",
                ingredients = listOf("Laitue", "Poulet", "Parmesan"), steps = listOf("Couper la laitue", "Griller le poulet")),
            Recipe("4Tarte aux pommes", "url_image2", "45 min", "Moyen",
                ingredients = listOf("Pommes", "Pâte", "Sucre"), steps = listOf("Peler les pommes", "Monter la tarte"))
        ))
    }

    fun setFilter(f: Filter) { _filter.value = f }

    fun toggleFavorite(recipe: Recipe) {
        recipe.isFavorite = !recipe.isFavorite
    }

    fun recordHistory(recipe: Recipe) {
        if (!history.contains(recipe)) history.add(recipe)
    }

    fun getFiltered(): List<Recipe> = when (filter) {
        Filter.ALL -> recipes
        Filter.DAILY -> recipes.take(3)  // par ex.
        Filter.SEARCH -> recipes.filter{
            it.name.contains(searchRecipes, ignoreCase = true)
        }         // implémenter une vraie recherche
        Filter.FAVORITES -> recipes.filter { it.isFavorite }
        Filter.HISTORY -> history
    }

    fun findById(id: Int): Recipe? = recipes.find { it.id == id }
}
