package com.example.culinar.viewmodels


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.culinar.models.Recipe
import com.example.culinar.models.viewModels.RECIPES_FIREBASE_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore// ajout pour communication avec la base de donnée
import com.google.firebase.ktx.Firebase //ajout pour communication avec la base de donnée


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

    private val db: FirebaseFirestore = Firebase.firestore

    // Mettre à jour le mot de recherche en fonction de la saisie de l'utilisateur
    fun UpdateSearchRecipes(query: String) {
        searchRecipes = query
    }



    init {

        fetchRecipesFromFirestore()
    }

    private fun fetchRecipesFromFirestore() {
        db.collection(RECIPES_FIREBASE_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                recipes.clear()
                for (document in result) {

                    val documentId = document.id

                    // recuperation des données des champs
                    val name = document.getString("name") ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val prepTime = document.getString("prepTime") ?: ""
                    val difficulty = document.getString("difficulty") ?: ""
                    val ingredients = document.get("ingredients") as? List<String> ?: emptyList()
                    val steps = document.get("steps") as? List<String> ?: emptyList()

                    val recipe = Recipe(
                        firestoreId = documentId,
                        name = name,
                        imageUrl = imageUrl,
                        prepTime = prepTime,
                        difficulty = difficulty,
                        ingredients = ingredients,
                        steps = steps
                    )
                    recipes.add(recipe)
                }
            }
            .addOnFailureListener { exception ->
                println("Erreur de récupération Firestore: ${exception.message}")
            }
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

    fun findById(id: String): Recipe? = recipes.find { it.firestoreId == id }

    fun addRecipe(recipe: Recipe) {
        recipes.add(recipe)

        // Ajouter la recette à Firestore
        val recipeData = hashMapOf(
            "name" to recipe.name,
            "imageUrl" to recipe.imageUrl,
            "prepTime" to recipe.prepTime,
            "difficulty" to recipe.difficulty,
            "ingredients" to recipe.ingredients,
            "steps" to recipe.steps
        )

        db.collection(RECIPES_FIREBASE_COLLECTION)
            .add(recipeData)
            .addOnSuccessListener { documentReference ->
                Log.d("RecipeViewModel", "Document ajouté avec ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.d("RecipeViewModel", "Erreur d'ajout du document", e)

            }
    }
}
