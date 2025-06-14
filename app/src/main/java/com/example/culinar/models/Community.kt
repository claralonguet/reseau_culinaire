package com.example.culinar.models

import com.google.firebase.database.IgnoreExtraProperties
import java.util.Date

@IgnoreExtraProperties
data class Community (
	var id: String = "",
	var name: String = "",
	var description: String = "",
	val author: String = "cd",
	var creationDate: Date = Date(),
	var members: List<String>? = null
) {
	constructor (name: String, description: String) : this() {
		this.name = name
		this.description = description
	}

	fun toMap(): Map<String, Any?> {
		return mapOf(
			"name" to name,
			"description" to description,
			"author" to author,
			"creationDate" to creationDate
		)
	}
}