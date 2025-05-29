package com.example.culinar.models.viewModels

import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import androidx.lifecycle.ViewModel

class FoodModel: ViewModel() {

	private val db = Firebase.firestore
	val foodItemsCollection = db.collection("Aliment").get()

}