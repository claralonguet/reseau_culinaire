package com.example.culinar.models.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinar.models.Community
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait

class CommunityViewModel : ViewModel() {

	private val db = Firebase.firestore
	/*TODO: Replace declaration using the actual user's id*/
	private val userId = "one"
	var allCommunities : MutableStateFlow<List<Community>> = MutableStateFlow<List<Community>>(listOf())
	var myCommunity: MutableState<Community?> = mutableStateOf(null)
	var selectedCommunity: Community? = null
	var myCommunities: MutableStateFlow<List<Community>> = MutableStateFlow<List<Community>>(listOf())
	// var isMember: MutableState<Boolean> = mutableStateOf(false)
	//var members: MutableStateFlow<List<String>> = MutableStateFlow<List<String>>(listOf())

	init {
		refreshCommunities()
	}

	fun refreshCommunities() {
		getCommunities()
		getMyCommunity()
	}

	fun selectCommunity(community: Community) {
		selectedCommunity = community
	}

	fun getCommunities() {
		db.collection("Communauté")
			.get()
			.addOnSuccessListener { result ->

				val toSort = mutableListOf<Community>()
				val communities = mutableListOf<Community>()

				for (document in result) {
					toSort.add(document.toObject<Community>(Community::class.java))
					toSort.last().id = document.id
				}
				Log.d("CommunityViewModel", "Retrieved ${toSort.size} communities")

				// Sorting communities (by membership)
				runBlocking {
					toSort.forEach { community ->
						if (getCommunityMembers(community.id).contains(userId))
							communities.add(0, community)
						else
							communities.add(community)
					}
				}
				Log.d("CommunityViewModel", "Sorted the communities")

				allCommunities.value = communities
			}
			.addOnFailureListener { exception ->
				Log.e("CommunityViewModel", "Error getting documents: $exception")
			}

	}

	fun getMyCommunity() {

		viewModelScope.launch {
			db.collection("Communauté")
				.document(userId)
				.get()
				.addOnSuccessListener { result ->
					myCommunity.value = result.toObject(Community::class.java)
				}
				.addOnFailureListener { exception ->
					Log.e("CommunityViewModel", "Error getting documents: $exception")
					myCommunity.value = null
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
		refreshCommunities()
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

	suspend fun getCommunityMembers(communityId: String): MutableList<String> {

		val fetchedMembers = mutableListOf<String>()
		db.collection("Communauté")
			.document(communityId)
			.collection("members")
			.get()
			.addOnSuccessListener { result ->
				for (document in result)
					fetchedMembers.add(document.id)

				Log.d("CommunityViewModel", "Retrieved ${fetchedMembers.size} members")
			}
			.addOnFailureListener { exception ->
				Log.e("CommunityViewModel", "Error getting members: $exception")
			}
			.await()
		return fetchedMembers
	}

	suspend fun checkMembership(communityId: String): Boolean {

		var isMember = false
		db.collection("Communauté")
			.document(communityId)
			.collection("members")
			.get()
			.addOnSuccessListener { result ->
				for (document in result)
					if (document.id == userId) {
						isMember = true
						Log.d("CommunityViewModel", "I'm a member!")
						break
					}
			}
			.addOnFailureListener { exception ->
				Log.e("CommunityViewModel", "Error getting members: $exception")
			}
			.await()
		return isMember

	}

	suspend fun addMember(communityId: String, userId: String = this.userId): Boolean {

		var success = false
		runBlocking {
			db.collection("Communauté")
				.document(communityId)
				.collection("members")
				.document(userId)
				.set(mapOf("userId" to userId))
				.addOnSuccessListener {
					Log.d("CommunityViewModel", "Member added successfully")
					success = true
				}
				.addOnFailureListener { exception ->
					Log.e("CommunityViewModel", "Error adding member: $exception")
				}
				.await()
		}
		Log.d("CommunityViewModel", "addMember: $success")
		return success
	}


}