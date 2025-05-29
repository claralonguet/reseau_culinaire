package com.example.culinar.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.culinar.models.Recipe

@Composable
fun RecipeDetailScreen(recipe: Recipe) {

    Column(modifier = Modifier.padding(bottom = 90.dp)){

        // Titre (nom de la recette)
        Row(modifier = Modifier
            .background(color = Color.Gray)
            .fillMaxWidth()
            .padding(5.dp),
            horizontalArrangement = Arrangement.Center) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {

            // Image du plat
            Image(
                painter = rememberAsyncImagePainter(recipe.imageUrl),
                contentDescription = recipe.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 16.dp)
            )

            // Temps de préparation et difficulté
            Text(
                text = "Temps : ${recipe.prepTime} | Difficulté : ${recipe.difficulty}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Liste des ingrédients
            Text(
                text = "Ingrédients :",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            recipe.ingredients.forEach { ingredient ->
                Text("- $ingredient")
            }

            Spacer(Modifier.height(16.dp))

            // Étapes de préparation
            Text(
                text = "Préparation :",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            recipe.steps.forEachIndexed { index, step ->
                Text("${index + 1}. $step")
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun RecipeDetailScreenPreview() {
    val fakeRecipe = Recipe(
        firestoreId = "1",
        name = "Pâtes Carbonara",
        imageUrl = "https://via.placeholder.com/400", // Image factice pour l'aperçu
        prepTime = "25 min",
        difficulty = "Facile",
        ingredients = listOf("Pâtes", "Œufs", "Lardons", "Parmesan"),
        steps = listOf(
            "Faire cuire les pâtes",
            "Préparer la sauce",
            "Mélanger les pâtes avec la sauce",
            "Servir chaud"
        )
    )
    RecipeDetailScreen(recipe = fakeRecipe)
}

