package com.example.culinar.recipesScreen.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.culinar.models.Recipe


@Composable
fun RecipeCard(
    recipe: Recipe,
    onToggleFavorite: () ->Unit,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(recipe.firestoreId) }
            .height(80.dp)
    ) {
        Row(Modifier.padding(8.dp)) {
           Image(
                painter = rememberAsyncImagePainter(recipe.imageUrl),
                contentDescription = recipe.name,
                modifier = Modifier.size(80.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text(text = recipe.name, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "${recipe.prepTime} • ${recipe.difficulty}", style = MaterialTheme.typography.bodyMedium)

                }
                Spacer(Modifier.width(5.dp))
                IconButton(onClick = { onToggleFavorite() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (recipe.isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                        tint = if (recipe.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun RecipeCardPreview() {
    // Exemple d'une recette
    val recipe = Recipe(
        firestoreId = "1",
        name = "Salade César",
        imageUrl = "https://www.mises-en-scene.fr/wp-content/uploads/2015/03/13.jpg", // Remplace par une URL d'image valide
        prepTime = "15 min",
        difficulty = "Facile"
    )

    // La fonction onClick ne fait rien dans le Preview
    RecipeCard(recipe = recipe,{} ){ /* Pas d'action ici pour le preview */ }
}

@Composable
fun  RecipeItem(
    recipe: Recipe,
    onClick: (String) -> Unit
){
    Row(modifier = Modifier.fillMaxWidth(1f)
        .clickable { onClick(recipe.firestoreId) }
        .background(
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.small
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)){
        Box(
            contentAlignment = Alignment.BottomEnd,
        ){
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(65.dp)
                    .clip(MaterialTheme.shapes.small),
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = recipe.name, style = MaterialTheme.typography.titleMedium)
        }
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "${recipe.prepTime} • ${recipe.difficulty}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
