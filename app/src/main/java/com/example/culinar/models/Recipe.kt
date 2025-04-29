package com.example.culinar.models


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class Recipe(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val prepTime: String,
    val difficulty: String,
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList()
) {
    var isFavorite by mutableStateOf(false)
}
