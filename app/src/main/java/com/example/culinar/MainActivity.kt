package com.example.culinar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.culinar.models.Recipe
import com.example.culinar.ui.components.Footer
import com.example.culinar.ui.components.Header
import com.example.culinar.ui.screens.RecipeDetailScreen
import com.example.culinar.ui.screens.RecipeListScreen
import com.example.culinar.ui.theme.CulinarTheme
import com.example.culinar.viewmodels.RecipeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CulinarTheme {
                Footer()
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
                Header()

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CulinarTheme {
        Footer()

        val viewModel = RecipeViewModel().apply {
            // Ajouter des recettes d'exemple
            recipes.addAll(
                listOf(
                    Recipe(3, "Salade Césarty 1", "url_image1", "15 min", "Facile", ingredients = listOf("Laitue", "Poulet", "Parmesan"), steps = listOf("Couper la laitue", "Griller le poulet")),
                    Recipe(4, "Tarte aux pommesty 2", "url_image2", "45 min", "Moyen", ingredients = listOf("Pommes", "Pâte", "Sucre"), steps = listOf("Peler les pommes", "Monter la tarte")),
                    Recipe(5, "Salade Césarty 3", "url_image1", "15 min", "Facile", ingredients = listOf("Laitue", "Poulet", "Parmesan"), steps = listOf("Couper la laitue", "Griller le poulet")),
                    Recipe(6, "Tarte aux pommesty 4", "url_image2", "45 min", "Moyen", ingredients = listOf("Pommes", "Pâte", "Sucre"), steps = listOf("Peler les pommes", "Monter la tarte")),
                    Recipe(7, "Salade Césarty 5", "url_image1", "15 min", "Facile", ingredients = listOf("Laitue", "Poulet", "Parmesan"), steps = listOf("Couper la laitue", "Griller le poulet")),
                    Recipe(8, "Tarte aux pommesty 6", "url_image2", "45 min", "Moyen", ingredients = listOf("Pommes", "Pâte", "Sucre"), steps = listOf("Peler les pommes", "Monter la tarte"))
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
        Header()

    }
}