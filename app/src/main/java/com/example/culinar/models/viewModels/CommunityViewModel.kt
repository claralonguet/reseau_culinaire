package com.example.culinar.models.viewModels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinar.models.Community
import com.example.culinar.models.Post
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CommunityViewModel : ViewModel() {

	private val db = Firebase.firestore

	// userId exposé en lecture seule (StateFlow)
	private val _userId = MutableStateFlow("")
	val userId: StateFlow<String> get() = _userId

	var allCommunities: MutableStateFlow<List<Community>> = MutableStateFlow(emptyList())
	var myCommunity: MutableState<Community?> = mutableStateOf(null)
	var selectedCommunity: Community? = null
	var isMember: MutableStateFlow<MutableMap<String, Boolean>> = MutableStateFlow(mutableMapOf())
	var myCommunities: MutableStateFlow<List<Community>> = MutableStateFlow(emptyList())
	var allPosts: MutableStateFlow<List<Post>> = MutableStateFlow(emptyList())

	init {
		viewModelScope.launch {
			_userId.collect { id ->
				if (id.isNotEmpty()) {
					refreshCommunities()
				}
			}
		}
	}

	fun setUserId(id: String) {
		_userId.value = id
	}

	fun refreshCommunities() {
		getCommunities()
		getMyCommunity()
		viewModelScope.launch {
			allCommunities.collect { communitiesList ->
				if (communitiesList.isNotEmpty()) {
					checkMemberships(communitiesList)
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
		viewModelScope.launch {
			try {
				val result = db.collection("Communauté").get().await()
				val toSort = mutableListOf<Community>()
				val communities = mutableListOf<Community>()

				for (document in result) {
					if (document.id != _userId.value) {
						val community = document.toObject(Community::class.java)
						community.id = document.id

						val memberIdsSnapshot = db.collection("Communauté")
							.document(document.id)
							.collection("members")
							.get()
							.await()
						val memberIds = memberIdsSnapshot.documents.map { it.id }
						community.members = memberIds.toMutableList()

						toSort.add(community)
					}
				}

				toSort.forEach { community ->
					if (community.members?.contains(_userId.value) == true)
						communities.add(0, community)
					else
						communities.add(community)
				}

				allCommunities.value = communities
				Log.d("CommunityViewModel", "Communities loaded: ${communities.size}")

			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error getting communities: $e")
			}
		}
	}

	fun getMyCommunity() {
		viewModelScope.launch {
			try {
				val result = db.collection("Communauté")
					.document(_userId.value)
					.get()
					.await()
				val community = result.toObject(Community::class.java)
				community?.id = _userId.value
				myCommunity.value = community
				Log.d("CommunityViewModel", "My community loaded")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error getting my community: $e")
				myCommunity.value = null
			}
		}
	}

	fun addCommunity(community: Community) {
		viewModelScope.launch {
			try {
				db.collection("Communauté")
					.document(_userId.value)
					.set(community)
					.await()
				addMember(community.id)
				refreshCommunities()
				Log.d("CommunityViewModel", "Community added")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error adding community: $e")
			}
		}
	}

	fun updateCommunity(community: Community) {
		viewModelScope.launch {
			try {
				db.collection("Communauté")
					.document(_userId.value)
					.set(community)
					.await()
				Log.d("CommunityViewModel", "Community updated")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error updating community: $e")
			}
		}
	}

	suspend fun getCommunityMembers(communityId: String): List<String> {
		return try {
			val snapshot = db.collection("Communauté")
				.document(communityId)
				.collection("members")
				.get()
				.await()
			snapshot.documents.map { it.id }
		} catch (e: Exception) {
			Log.e("CommunityViewModel", "Error getting members for $communityId: $e")
			emptyList()
		}
	}

	suspend fun checkMemberships(communitiesToScan: List<Community>) {
		if (communitiesToScan.isEmpty()) {
			isMember.value = mutableMapOf()
			return
		}
		val currentMembership = isMember.value.toMutableMap()
		for (community in communitiesToScan) {
			try {
				val membersSnapshot = db.collection("Communauté")
					.document(community.id)
					.collection("members")
					.get()
					.await()
				currentMembership[community.id] = membersSnapshot.documents.any { it.id == _userId.value }
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error checking membership for ${community.id}: $e")
				currentMembership[community.id] = false
			}
		}
		isMember.value = currentMembership
	}

	suspend fun addMember(communityId: String, userId: String = _userId.value): Boolean {
		return try {
			db.collection("Communauté")
				.document(communityId)
				.collection("members")
				.document(userId)
				.set(mapOf("userId" to userId))
				.await()

			val currentCommunities = allCommunities.value.toMutableList()
			currentCommunities.find { it.id == communityId }?.let {
				it.members = (it.members ?: mutableListOf()).toMutableList().apply { add(userId) }
			}
			allCommunities.value = currentCommunities

			checkMemberships(currentCommunities)
			Log.d("CommunityViewModel", "Member added to $communityId")
			true
		} catch (e: Exception) {
			Log.e("CommunityViewModel", "Error adding member to $communityId: $e")
			false
		}
	}

	suspend fun removeMember(communityId: String, userId: String = _userId.value): Boolean {
		return try {
			db.collection("Communauté")
				.document(communityId)
				.collection("members")
				.document(userId)
				.delete()
				.await()

			val currentCommunities = allCommunities.value.toMutableList()
			currentCommunities.find { it.id == communityId }?.let {
				it.members = (it.members ?: mutableListOf()).toMutableList().apply { remove(userId) }
			}
			allCommunities.value = currentCommunities

			checkMemberships(currentCommunities)
			Log.d("CommunityViewModel", "Member removed from $communityId")
			true
		} catch (e: Exception) {
			Log.e("CommunityViewModel", "Error removing member from $communityId: $e")
			false
		}
	}

	fun getPosts(communityId: String) {
		viewModelScope.launch {
			try {
				val result = db.collection("Communauté")
					.document(communityId)
					.collection("posts")
					.get()
					.await()
				val fetchedPosts = result.documents.map { doc ->
					val post = doc.toObject(Post::class.java)
					post?.id = doc.id
					post
				}.filterNotNull()
				allPosts.value = fetchedPosts
				Log.d("CommunityViewModel", "Posts loaded for $communityId, count: ${fetchedPosts.size}")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error getting posts for $communityId: $e")
				allPosts.value = emptyList()
			}
		}
	}

	fun hasLiked(post: Post): Boolean {
		return post.likes.contains(_userId.value)
	}

	fun createPost(post: Post, communityId: String = myCommunity.value?.id ?: "") {
		if (communityId.isEmpty()) {
			Log.e("CommunityViewModel", "Cannot create post: communityId is empty")
			return
		}
		viewModelScope.launch {
			try {
				db.collection("Communauté")
					.document(communityId)
					.collection("posts")
					.add(post)
					.await()
				getPosts(communityId)
				Log.d("CommunityViewModel", "Post created in $communityId")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error creating post in $communityId: $e")
			}
		}
	}

	// --- Nouvelle fonction : ajoute un like au post ---
	fun likePost(post: Post, userId: String) {
		viewModelScope.launch {
			try {
				val postRef = db.collection("Communauté")
					.document(selectedCommunity?.id ?: return@launch)
					.collection("posts")
					.document(post.id)

				val updatedLikes = (post.likes.toMutableList()).apply {
					if (!contains(userId)) add(userId)
				}

				postRef.update("likes", updatedLikes).await()
				// Met à jour localement le post dans allPosts
				updateLocalPostLikes(post.id, updatedLikes)
				Log.d("CommunityViewModel", "Post liked by $userId")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error liking post: $e")
			}
		}
	}

	// --- Nouvelle fonction : enlève un like au post ---
	fun unlikePost(post: Post, userId: String) {
		viewModelScope.launch {
			try {
				val postRef = db.collection("Communauté")
					.document(selectedCommunity?.id ?: return@launch)
					.collection("posts")
					.document(post.id)

				val updatedLikes = (post.likes.toMutableList()).apply {
					remove(userId)
				}

				postRef.update("likes", updatedLikes).await()
				// Met à jour localement le post dans allPosts
				updateLocalPostLikes(post.id, updatedLikes)
				Log.d("CommunityViewModel", "Post unliked by $userId")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error unliking post: $e")
			}
		}
	}

	// Met à jour localement le nombre de likes pour un post donné dans allPosts
	private fun updateLocalPostLikes(postId: String, updatedLikes: List<String>) {
		val updatedPosts = allPosts.value.map {
			if (it.id == postId) it.copy(likes = updatedLikes.toMutableList()) else it
		}
		allPosts.value = updatedPosts
	}
}
