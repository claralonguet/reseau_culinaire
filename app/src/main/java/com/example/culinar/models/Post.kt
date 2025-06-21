package com.example.culinar.models

import com.google.firebase.database.IgnoreExtraProperties
import java.util.Date

@IgnoreExtraProperties
data class Post(
	var id: String = "",
	var name: String = "",
	var content: String = "",
	var likes: List<String> = listOf(),
	val date: Date = Date(),
	val imageUri: String = "",
	val authorId: String = "",
	val isPrivate: Boolean = true,
	val communityId: String = "",
)