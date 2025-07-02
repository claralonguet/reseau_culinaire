package com.example.culinar.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import java.util.Date

@IgnoreExtraProperties
data class Post(
	var id: String = "",
	var name: String = "",
	var content: String = "",
	var likes: List<String> = listOf(),
	val date: Date = Date(),
	val imageUri: String = "",
	var authorId: String = "",
	@set:PropertyName("isPrivate")
	var isPrivate: Boolean = true,
	var communityId: String = "",
) {
	@get:Exclude
	@set:Exclude
	var username: String = "Utilisateur inconnu"

	fun toMap(): HashMap<String, Any?> {
		return hashMapOf(
			"id" to id,
			"name" to name,
			"content" to content,
			"likes" to likes,
			"date" to date,
			"imageUri" to imageUri,
			"authorId" to authorId,
			"isPrivate" to isPrivate,
			"communityId" to communityId,
			"username" to username

		)
	}
}

