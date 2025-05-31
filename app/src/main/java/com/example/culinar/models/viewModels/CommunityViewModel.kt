package com.example.culinar.models.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinar.models.Community
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait

class CommunityViewModel : ViewModel() {

	private val db = Firebase.firestore
	private val userId = "one"
	var allCommunities : MutableStateFlow<List<Community>> = MutableStateFlow<List<Community>>(listOf())
	var myCommunity: Community? = null

	init {
		refreshCommunities()
	}

	fun refreshCommunities() {
		getCommunities()
		getMyCommunity()
	}


	fun getCommunities() {
		viewModelScope.launch {
			db.collection("Communauté")
				.get()
				.addOnSuccessListener { result ->
					val communities = mutableListOf<Community>()
					for (document in result)
						communities.add(document.toObject(Community::class.java))
					allCommunities.value = communities
				}
				.addOnFailureListener { exception ->
					Log.e("CommunityViewModel", "Error getting documents: $exception")
				}
				.await()
		}
	}

	fun getMyCommunity() {

		viewModelScope.launch {
			db.collection("Communauté")
				.document(userId)
				.get()
				.addOnSuccessListener { result ->
					myCommunity = result.toObject(Community::class.java)
				}
				.addOnFailureListener { exception ->
					Log.e("CommunityViewModel", "Error getting documents: $exception")
					myCommunity = null
				}
				.await()
		}
	}

	fun addCommunity(community: Community) {

		viewModelScope.launch {
			db.collection("Communauté")
				.document(userId)
				.set(community)
				.addOnSuccessListener {
					Log.d("CommunityViewModel", "Community added successfully")
				}
				.addOnFailureListener { exception ->
					Log.e("CommunityViewModel", "Error adding community: $exception")
				}
				.await()
		}
	}

	fun updateCommunity(community: Community) {

		viewModelScope.launch {
			db.collection("Communauté")
				.document(userId)
				.set(community)
				.addOnSuccessListener {
					Log.d("CommunityViewModel", "Community updated successfully")
				}
				.addOnFailureListener { exception ->
					Log.e("CommunityViewModel", "Error updating community: $exception")
				}
				.await()
		}
	}

}