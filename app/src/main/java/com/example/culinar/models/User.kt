package com.example.culinar.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User (var username: String, var email: String, var password: String) {

	fun toMap(): Map<String, Any?> {
		return mapOf(
			"username" to username,
			"email" to email,
			"password" to password
		)
	}
}