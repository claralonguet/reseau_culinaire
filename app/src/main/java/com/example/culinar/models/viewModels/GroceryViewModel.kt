package com.example.culinar.models.viewModels

import android.util.Log
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import androidx.lifecycle.ViewModel
import com.example.culinar.models.Aliment
import com.example.culinar.models.FoodItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class GroceryViewModel: ViewModel() {

	private val rdb = Firebase.database.reference
	private val db = Firebase.firestore
	private val userId = "one"

	var groceryItems: MutableStateFlow<List<Aliment>> = MutableStateFlow<List<Aliment>>(listOf())
	val allFoodItems = mutableListOf<Aliment>()

	val dbListener = object : ValueEventListener {
		override fun onDataChange(dataSnapshot: DataSnapshot) {

		}

		override fun onCancelled(databaseError: DatabaseError) {
			// Getting Post failed, log a message
			Log.w("GroceryViewModel", "loadPost:onCancelled", databaseError.toException())
		}
	}

    init {
		loadFoodItems()
	}

	fun loadFoodItems() {
		// Loading every food item from the database
		db.collection("Aliment")
			.get()
			.addOnSuccessListener { aliments ->
				if (aliments != null) {
					Log.d("GroceryViewModel", "Retrieved food items successfully.")
					aliments.forEach { item ->
						//val details = item.get("details") as? Map<*, *>
						val aliment = item.toObject(FoodItem::class.java)
						if (aliment.details != null)
						allFoodItems.add(
								aliment.details
						/*Aliment(
								name = details?.get("name") as? String ?: "",
								unit = details?.get("unit") as? String ?: ""
							)*/
						)
					}
				} else {
					Log.d("GroceryViewModel", "No food items found.")
				}
			}
			.addOnFailureListener {
				Log.d("GroceryViewModel", "Error getting food items.")
			}


		db.collection("GroceryList").document(userId).collection("items")
			.get()
			.addOnSuccessListener { aliments ->
				if (aliments != null) {
					Log.d("GroceryViewModel", "Retrieved grocery list items successfully.")
					aliments.forEach { item ->
						//val details = item.get("details") as? Map<*, *>
						val aliment = item.toObject(FoodItem::class.java)
						groceryItems.value = groceryItems.value + aliment.details!!
						/*
						+ Aliment(
							name = details?.get("name") as? String ?: "",
							unit = details?.get("unit") as? String ?: "",
							quantity = details?.get("quantity") as? Int ?: 1
						)*/
					}
				} else {
					Log.d("GroceryViewModel", "No grocery list items found.")
				}
			}
			.addOnFailureListener {
				Log.d("GroceryViewModel", "Error getting grocery list items.")
			}
	}

	// Loadig every grocery item from the database (for the current user)
    fun addItemToGroceryList(item: Aliment) {
		groceryItems.value = groceryItems.value + item

		// Create a map to hold the "details"
		// Create the main document data with the "details" map
		val documentData = mapOf(
			"details" to item.toMap()
		)
		try {
			db.collection("GroceryList")
				.document(userId)
				.collection("items")
				.document(item.name) // Use aliment name as document ID
				.set(documentData) // .set() overwrites the document or creates it
				//.await() // Use await for suspending function


			Log.d("GroceryViewModel", "Item '${item.name}' successfully written/updated in Firestore.")
			// No need to manually update local _groceryItems.value if using addSnapshotListener,
			// as it will automatically reflect the change.
			// If not using addSnapshotListener, you would update it here.
		} catch (e: Exception) {
			Log.e("GroceryViewModel", "Error writing item '${item.name}' to Firestore", e)
		}

		//db.collection("GroceryList").document(userId).collection("items").document(item.name).set(documentData)
    }

    fun removeItemToGroceryList(item: Aliment) {
		groceryItems.value = groceryItems.value.filter { it.name != item.name }
		db.collection("GroceryList").document(userId).collection("items").document(item.name).delete()
			.addOnFailureListener {
				Log.d("GroceryViewModel", "Error deleting item '${item.name}' in Firestore.")
			}

    }

	fun modifyItemToGroceryList(item: Aliment) {
		groceryItems.value = groceryItems.value.filter { it.name != item.name } + item
		//rdb.child("GroceryList").child(userId).child("items").child(item.name).child("details").setValue(item.toMap())
		db.collection("GroceryList").document(userId).collection("items").document(item.name).update("details", item.toMap())
			.addOnFailureListener {
				Log.d("GroceryViewModel", "Error modifying item '${item.name}' in Firestore.")
			}

	}

}