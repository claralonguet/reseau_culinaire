package com.example.culinar.models.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.isEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinar.models.Community
import com.example.culinar.models.Post
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait
import java.util.Dictionary
import java.util.Hashtable

class CommunityViewModel : ViewModel() {

	private val db = Firebase.firestore
	/*TODO: Replace declaration using the actual user's id*/
	val userId = "one"
	var allCommunities : MutableStateFlow<List<Community>> = MutableStateFlow<List<Community>>(listOf())
	var myCommunity: MutableState<Community?> = mutableStateOf(null)
	var selectedCommunity: Community? = null
	var isMember: MutableStateFlow<MutableMap<String, Boolean>> = MutableStateFlow<MutableMap<String, Boolean>>(
		mutableMapOf())
	var myCommunities: MutableStateFlow<List<Community>> = MutableStateFlow<List<Community>>(listOf())
	var allPosts: MutableStateFlow<List<Post>> = MutableStateFlow<List<Post>>(listOf())

	init {
		refreshCommunities()
	}


	// Communities

	fun refreshCommunities() {
		getCommunities()
		getMyCommunity()
		viewModelScope.launch {
			allCommunities.collect { communitiesList -> // Collect emissions from allCommunities
				if (communitiesList.isNotEmpty()) {
					Log.d("CommunityViewModel", "allCommunities has data, now checking memberships. Size: ${communitiesList.size}")
					checkMemberships(communitiesList) // Pass the list to avoid race condition
				} else {
					Log.d("CommunityViewModel", "allCommunities is empty, skipping membership check for now.")
				}
			}
		}
	}

	fun selectCommunity(community: Community) {
		selectedCommunity = community
		Log.d("CommunityViewModel", "Community selected: ${community.id}. Fetching posts.")
		getPosts(community.id)
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

					// Now, fetching member IDs for this community
					runBlocking {
						val memberIdsSnapshot = db.collection("Communauté")
							.document(document.id)
							.collection("members") // Members subcollection
							.get()
							.await()

						val memberIds =
							memberIdsSnapshot.documents.map { it.id } // Getting the IDs of the documents in the subcollection
						toSort.last().members = memberIds // Assigning the list
					}
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
			refreshCommunities()
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


	// Members

	suspend fun getCommunityMembers(communityId: String): MutableList<String> {

		val fetchedMembers = mutableListOf<String>()
		db.collection("Communauté")
			.document(communityId)
			.collection("members")
			.get()
			.addOnSuccessListener { result ->
				for (document in result)
					fetchedMembers.add(document.id)

				Log.d("CommunityViewModel", "Retrieved ${fetchedMembers.size} members in community $communityId")
			}
			.addOnFailureListener { exception ->
				Log.e("CommunityViewModel", "Error getting members: $exception")
			}
			.await()
		return fetchedMembers
	}

	suspend fun checkMemberships(communitiesToScan: List<Community>) {

		 if (communitiesToScan.isEmpty()) {
			 Log.d("CommunityViewModel", "checkMembershipsInternal called with empty list.")
			 isMember.value = mutableMapOf() // Ensure it's reset if the list is empty
			 return
		 }

		 // Updating the membership state of the current user in the community
		 val currentMembership = isMember.value.toMutableMap() // Creating a copy of the membership states

		 for (community in communitiesToScan) {
			 try {
				 val membersSnapshot = db.collection("Communauté")
					 .document(community.id)
					 .collection("members")
					 .get()
					 .await()


				 var userIsMemberInThisCommunity = false
				 for (memberDoc in membersSnapshot.documents) {
					 if (memberDoc.id == userId) {
						 userIsMemberInThisCommunity = true
						 Log.d("CommunityViewModel", "User $userId is a member of ${community.id}!")
						 break
					 }
				 }
				 currentMembership[community.id] = userIsMemberInThisCommunity
				 /*
				 if (userIsMemberInThisCommunity) {
					 Log.d("CommunityViewModel", "Setting isMember for ${community.id} to true")
				 } else {
					 Log.d("CommunityViewModel", "Setting isMember for ${community.id} to false (or not found)")
				 }
				 */

			 } catch (e: Exception) {
				 Log.e("CommunityViewModel", "Error getting members for ${community.id}: $e")
				 currentMembership[community.id] = false // Default to false on error
			 }
		 }
		 isMember.value = currentMembership // Assign the new map
		 //Log.d("CommunityViewModel", "isMemberState: ${isMember.value}")

	 }

	suspend fun updateMembership(communityId: String, userId: String = this.userId) {

		viewModelScope.launch {
			db.collection("Communauté")
				.document(communityId)
				.collection("members")
				.get()
				.addOnSuccessListener {
					val memberships: MutableMap<String, Boolean> = isMember.value.toMutableMap()
					memberships[communityId] = false
					for (document in it) {
						if (document.id == userId) {
							memberships[communityId] = true
						}
						break
					}
					isMember.value = memberships
				}
				.addOnFailureListener { exception ->
					Log.e("CommunityViewModel", "Error getting members: $exception")
				}
				.await()
		}
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

			// Updating the number of members in the community
			var currentCommunities = allCommunities.value
			currentCommunities.find { it.id == communityId }?.let {
				it.members = it.members?.plus(userId)
			} ?: allCommunities.value
			allCommunities.value = currentCommunities

			updateMembership(communityId)
		}
		Log.d("CommunityViewModel", "addMember: $success")
		return success
	}

	suspend fun removeMember(communityId: String, userId: String = this.userId): Boolean {
		var success = false
		runBlocking {
			db.collection("Communauté")
				.document(communityId)
				.collection("members")
				.document(userId)
				.delete()
				.addOnSuccessListener {
					Log.d("CommunityViewModel", "Member removed successfully")
					success = true
				}
				.addOnFailureListener { exception ->
					Log.e("CommunityViewModel", "Error removing member: $exception")
				}
				.await()

			// Updating the number of members in the community
			var currentCommunities = allCommunities.value
			currentCommunities.find { it.id == communityId }?.let {
				it.members = it.members?.minus(userId)
			} ?: allCommunities.value
			allCommunities.value = currentCommunities
			}

		updateMembership(communityId)

		Log.d("CommunityViewModel", "removeMember: $success")
		return success
	}


	// Posts

	fun getPosts(communityId: String) {
		try {
			viewModelScope.launch {
				db.collection("Communauté")
					.document(communityId)
					.collection("posts")
					.get()
					.addOnSuccessListener { result ->
						val fetchedPosts = mutableListOf<Post>()
						for (document in result) {
							fetchedPosts.add(document.toObject(Post::class.java))
							fetchedPosts.last().id = document.id
						}
						Log.d("ViewModelGetPosts", "Fetched ${fetchedPosts.size} posts. IDs: ${fetchedPosts.map { it.id }}")
						allPosts.value = fetchedPosts
						Log.d("ViewModelGetPosts", "allPosts.value updated. New size: ${allPosts.value.size}. New IDs: ${allPosts.value.map { it.id }}")
					}
					.addOnFailureListener { exception ->
						Log.e("CommunityViewModel", "Error getting documents: $exception")
						allPosts.value = emptyList()
					}
					.await()
			}
		} catch (e: Exception) {
			Log.e("CommunityViewModel", "Error getting posts: $e")
			allPosts.value = emptyList() // Also good
		}
	}



}