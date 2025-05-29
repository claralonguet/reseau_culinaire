package com.example.culinar.models


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random
import kotlin.random.nextInt

data class Recipe(
    val name: String,
    val imageUrl: String,
    val prepTime: String,
    val difficulty: String,
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val firestoreId: String = ""
) {
    var isFavorite by mutableStateOf(false)
}
