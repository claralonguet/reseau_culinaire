package com.example.culinar.RecipesScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.culinar.models.Recipe
import com.example.culinar.ui.screens.RecipeDetailScreen
import com.example.culinar.ui.screens.RecipeListScreen

@Composable
fun RecipesScreen() {
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
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "recipeList") {
        composable("recipeList") {
            RecipeListScreen(navController = navController, vm = viewModel)
        }
        composable("recipeDetail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            val recipe = viewModel.findById(id ?: 0)
            if (recipe != null) {
                RecipeDetailScreen(recipe = recipe)
            }
        }
    }
}


@Preview (showBackground = true)
@Composable
fun RecipesScreenPreview() {
    RecipesScreen()
}



