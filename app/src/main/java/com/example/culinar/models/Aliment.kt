package com.example.culinar.models

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Aliment (
	var name: String = "aliment",
	var unit: String = "unité",
	var quantity: Int? = null,
	var photo: String? = null
) {

	constructor(name: String, unit: String, quantity: Int, photo: String?) : this() {
		this.name = name
		this.unit = unit
		this.quantity = quantity
		this.photo = photo
	}

	fun toMap(): Map<String, Any?> {
		return mapOf(
			"name" to name,
			"unit" to unit,
			"quantity" to quantity,
			"photo" to photo
		)
	}
}

@IgnoreExtraProperties
data class FoodItem (
	val details: Aliment? = null
)