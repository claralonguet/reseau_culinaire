package com.example.culinar.viewmodels


import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.culinar.models.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore// ajout pour communication avec la base de donnée
import com.google.firebase.ktx.Firebase //ajout pour communication avec la base de donnée
import com.google.firebase.auth.ktx.auth
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


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
//recup des infos de l'user
    private val _username = mutableStateOf<String?>(null)
    val username: String?
        get() = _username.value

    private val _userId = mutableStateOf<String?>(null)
    val userId: String?
        get() = _userId.value

    private val _isAdmin = mutableStateOf<Boolean>(false)
    val isAdmin: Boolean
        get() = _isAdmin.value


    private fun fetchUserData(username: String) {
        db.collection("Utilisateur")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDoc = documents.documents[0]

                    // Stocker les infos
                    _userId.value = userDoc.id
                    _isAdmin.value = userDoc.getBoolean("admin") ?: false
                    _username.value = username
                }
            }
            .addOnFailureListener {
                println("Erreur lors de la récupération de l'utilisateur : ${it.message}")
            }
    }





    init {

        fetchRecipesFromFirestore()
    }


    private fun fetchRecipesFromFirestore() {
        db.collection("Recette")
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
}
