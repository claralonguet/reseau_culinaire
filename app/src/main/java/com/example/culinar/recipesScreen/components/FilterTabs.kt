package com.example.culinar.recipesScreen.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.culinar.viewmodels.Filter
import com.example.culinar.viewmodels.RecipeViewModel

@Composable
fun FilterTabs(
    current: Filter,
    onFilterChange: (Filter) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Filter.entries.forEach { filter ->
            val isSelected = (filter == current)

            Button(
                modifier = Modifier.selectable(
                    selected = isSelected,
                    onClick = { onFilterChange(filter) },
                ),
                onClick = { onFilterChange(filter) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    contentColor = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    },
                )
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
                Text(
                    text = when (filter) {
                        Filter.ALL -> "Tout"
                        Filter.SEARCH -> "Recherche"
                        Filter.HISTORY -> "Historique"
                        Filter.FAVORITES -> "Favoris"
                        Filter.DAILY -> "Recette du jour"
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterTabsPreview() {
    val viewModel = RecipeViewModel() // Crée une instance du ViewModel dans le preview
    var currentFilter by remember { mutableStateOf(Filter.ALL) }

    // Simulation de changement de filtre
    FilterTabs(
        current = currentFilter,
        onFilterChange = { selectedFilter ->
            currentFilter = selectedFilter
            viewModel.setFilter(selectedFilter) // Mettez à jour le filtre dans le ViewModel
        }
    )
}




