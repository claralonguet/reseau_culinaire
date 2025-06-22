package com.example.culinar.models

import java.util.Date

data class RecettePost(
    var id: String = "",
    var content: String = "",
    var likes: List<String> = listOf(),
    val date: Date = Date(),
    val imageUri: String = "",
    val authorId: String = "",
    val private: Boolean = false,
)
