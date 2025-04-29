package com.example.culinar.ui.screens

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.culinar.models.Recipe
import com.example.culinar.ui.components.FilterTabs
import com.example.culinar.ui.components.RecipeCard
import com.example.culinar.ui.components.RecipeItem
import com.example.culinar.viewmodels.Filter
import com.example.culinar.viewmodels.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(navController: NavController, vm: RecipeViewModel) {
    // On récupère le filtre et les recettes filtrées depuis le ViewModel
    val filter by remember{ vm::filter }
    val list = vm.getFiltered()
    var currentFilter by remember { mutableStateOf(filter) }

    // Structure de la page avec un app bar et un lazy column
    Column(Modifier.fillMaxSize()) {
        // AppBar avec le titre
        TopAppBar(title = { Text("Recettes") })

        // Composant FilterTabs pour changer le filtre
        //FilterTabs(current = filter, onFilterChange ={ vm::setFilter})

        FilterTabs(
        current = currentFilter,
        onFilterChange = { selectedFilter ->
            currentFilter = selectedFilter
            vm.setFilter(selectedFilter) // Mettez à jour le filtre dans le ViewModel
        }
    )

        // Liste des recettes avec LazyColumn
        LazyColumn(Modifier.fillMaxSize()) {
            items(list.size) { i ->
                // Carte pour chaque recette avec un événement de clic (ici sans navigation)
                RecipeCard(recipe = list[i], onToggleFavorite = {
                    vm.toggleFavorite(list[i])
                }) { id ->
                    vm.recordHistory(list[i])
                    navController.navigate("recipeDetail/$id")
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun RecipeListScreenPreview() {
    // Initialisation du ViewModel avec des données d'exemple
    val viewModel = RecipeViewModel().apply {
        // Ajouter des recettes d'exemple
        recipes.addAll(
            listOf(
                Recipe(3, "Salade Césartyy", "url_image1", "15 min", "Facile", ingredients = listOf("Laitue", "Poulet", "Parmesan"), steps = listOf("Couper la laitue", "Griller le poulet")),
                Recipe(4, "Tarte aux pommestyy", "url_image2", "45 min", "Moyen", ingredients = listOf("Pommes", "Pâte", "Sucre"), steps = listOf("Peler les pommes", "Monter la tarte"))
            )
        )
    }

    // Passer le ViewModel à la RecipeListScreen
    RecipeListScreen(vm = viewModel)
}*/

