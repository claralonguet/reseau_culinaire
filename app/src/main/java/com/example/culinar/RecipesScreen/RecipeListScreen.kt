package com.example.culinar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.culinar.models.Screen
import com.example.culinar.ui.components.FilterTabs
import com.example.culinar.ui.components.RecipeCard
import com.example.culinar.ui.theme.Typography
import com.example.culinar.ui.theme.grey
import com.example.culinar.ui.theme.mediumGreen
import com.example.culinar.viewmodels.Filter
import com.example.culinar.viewmodels.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(navController: NavHostController = rememberNavController(), vm: RecipeViewModel = viewModel ()) {
    // On récupère le filtre et les recettes filtrées depuis le ViewModel
    val filter by remember{ vm::filter }
    val list = vm.getFiltered()
    var currentFilter by remember { mutableStateOf(filter) }
    var searchFilterRecipes by remember { mutableStateOf("") }

    // Structure de la page avec un app bar et un lazy column
    Column(Modifier.fillMaxSize()) {
        // AppBar avec le titre
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = grey)
        ) {
            Text(
                text = "Recettes",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )
        }

        // Composant FilterTabs pour changer le filtre
        //FilterTabs(current = filter, onFilterChange ={ vm::setFilter})

        FilterTabs(
        current = currentFilter,
        onFilterChange = { selectedFilter ->
            currentFilter = selectedFilter
            vm.setFilter(selectedFilter) // Mettez à jour le filtre dans le ViewModel
            searchFilterRecipes = selectedFilter.name
        }
    )
        Spacer(modifier = Modifier.height(5.dp))
        if ( searchFilterRecipes == Filter.SEARCH.name) {
            OutlinedTextField(
                value = vm.searchRecipes,
                onValueChange = {
                    vm.UpdateSearchRecipes(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text("Rechercher une recette") },
                singleLine = true
            )
       }


        // Liste des recettes avec LazyColumn
        LazyColumn(Modifier.fillMaxSize().padding(bottom = 90.dp)) {
            items(list.size) { i ->
                // Carte pour chaque recette avec un événement de clic (ici sans navigation)
                RecipeCard(recipe = list[i], onToggleFavorite = {
                    vm.toggleFavorite(list[i])
                }) { id ->
                    vm.recordHistory(list[i])
                    navController.navigate("${Screen.RecipeDetail.name}/$id")
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun RecipeListScreenPreview() {
    RecipeListScreen()
}

