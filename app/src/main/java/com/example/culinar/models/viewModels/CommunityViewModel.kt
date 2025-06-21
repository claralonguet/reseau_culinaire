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

	// --- Met à jour l'ID de l'utilisateur ---
	fun setUserId(id: String) {
		_userId.value = id
	}

	// --- Rafraîchit les communautés ---
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

	// --- Sélectionne une communauté ---
	fun selectCommunity(community: Community) {
		selectedCommunity = community
		Log.d("CommunityViewModel", "Community selected: ${community.id}. Fetching posts.")
		getPosts(community.id)
	}

	// --- Récupère toutes les communautés ---
	fun getCommunities() {
		viewModelScope.launch {
			try {
				val result = db.collection(COMMUNITY_FIREBASE_COLLECTION).get().await()
				val toSort = mutableListOf<Community>()
				val communities = mutableListOf<Community>()

				for (document in result) {
					if (document.id != _userId.value) {
						val community = document.toObject(Community::class.java)
						community.id = document.id

						val memberIdsSnapshot = db.collection(COMMUNITY_FIREBASE_COLLECTION)
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

	// --- Récupère la communauté de l'utilisateur, si elle existe ---
	fun getMyCommunity() {
		viewModelScope.launch {
			try {
				val result = db.collection(COMMUNITY_FIREBASE_COLLECTION)
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

	// --- Ajoute une communauté ---
	fun addCommunity(community: Community) {
		viewModelScope.launch {
			try {
				db.collection(COMMUNITY_FIREBASE_COLLECTION)
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

	// --- Met à jour une communauté ---
	fun updateCommunity(community: Community) {
		viewModelScope.launch {
			try {
				db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(_userId.value)
					.set(community)
					.await()
				Log.d("CommunityViewModel", "Community updated")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error updating community: $e")
			}
		}
	}

	// --- Récupère les membres d'une communauté ---
	suspend fun getCommunityMembers(communityId: String): List<String> {
		return try {
			val snapshot = db.collection(COMMUNITY_FIREBASE_COLLECTION)
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

	// --- Vérifie si l'utilisateur est membre d'une communauté ---
	suspend fun checkMemberships(communitiesToScan: List<Community>) {
		if (communitiesToScan.isEmpty()) {
			isMember.value = mutableMapOf()
			return
		}
		val currentMembership = isMember.value.toMutableMap()
		for (community in communitiesToScan) {
			try {
				val membersSnapshot = db.collection(COMMUNITY_FIREBASE_COLLECTION)
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

	// --- Ajoute un membre à une communauté ---
	suspend fun addMember(communityId: String, userId: String = _userId.value): Boolean {
		return try {
			db.collection(COMMUNITY_FIREBASE_COLLECTION)
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

	// --- Supprime un membre d'une communauté ---
	suspend fun removeMember(communityId: String, userId: String = _userId.value): Boolean {
		return try {
			db.collection(COMMUNITY_FIREBASE_COLLECTION)
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

	// --- Récupère les posts d'une communauté ---
	fun getPosts(communityId: String) {
		viewModelScope.launch {
			try {
				val result = db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(communityId)
					.collection("posts")
					.get()
					.await()
				val fetchedPosts = result.documents.mapNotNull { doc ->
					val post = doc.toObject(Post::class.java)
					post?.id = doc.id
					post
				}
				allPosts.value = fetchedPosts
				Log.d("CommunityViewModel", "Posts loaded for $communityId, count: ${fetchedPosts.size}")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error getting posts for $communityId: $e")
				allPosts.value = emptyList()
			}
		}
	}

	// --- Vérifie si l'utilisateur a déjà liké un post ---
	fun hasLiked(post: Post): Boolean {
		return post.likes.contains(_userId.value)
	}

	// --- Crée un post ---
	fun createPost(post: Post, communityId: String = myCommunity.value?.id ?: "") {
		if (communityId.isEmpty()) {
			Log.e("CommunityViewModel", "Cannot create post: communityId is empty")
			return
		}
		post.authorId = _userId.value
		post.communityId = communityId

		viewModelScope.launch {
			try {
				val postRef = db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(communityId)
					.collection("posts")
					.add(post)
					.addOnSuccessListener {
						it.update(mapOf("id" to it.id))
					}
					.await()
				post.id = postRef.id
				getPosts(communityId)
				Log.d("CommunityViewModel", "Post created in $communityId")
			} catch (e: Exception) {
				Log.d("CommunityViewModel", "Error creating post in $communityId: $e")
			}

			// If the post is set to public, add it to the public feed
			if (!post.isPrivate) {
				try {
					db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
						.document(post.id).set(post.toMap())
						.await()
					Log.d("CommunityViewModel", "Post added to public feed")
				} catch (e: Exception) {
					Log.d("CommunityViewModel", "Error adding post to public feed: $e")
				}
			}

		}

	}

	// --- Nouvelle fonction : ajoute un like au post ---
	fun likePost(post: Post, userId: String) {

		val updatedLikes = mutableListOf<String>()

		viewModelScope.launch {
			try {
				val postRef = db.collection(COMMUNITY_FIREBASE_COLLECTION)
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
				Log.d("CommunityViewModel", "Error liking post: $e")
			}

			// If the post is public, update likes of the public version (on the public feed)
			if (post.isPrivate == false) {
				try {
					db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
						.document(post.id)
						.update("likes", updatedLikes)
						.await()
					Log.d("CommunityViewModel", "Post likes updated in public feed")
				} catch (e: Exception) {
					Log.d("CommunityViewModel", "Error updating post likes in public feed: $e")

				}
			}
		}
	}

	// --- Nouvelle fonction : enlève un like au post ---
	fun unlikePost(post: Post, userId: String) {

		val updatedLikes = mutableListOf<String>()

		viewModelScope.launch {
			try {
				val postRef = db.collection(COMMUNITY_FIREBASE_COLLECTION)
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
				Log.d("CommunityViewModel", "Error unliking post: $e")
			}

			// If the post is public, update likes of the public version (on the public feed)
			if (!post.isPrivate) {
				try {
					db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
						.document(post.id)
						.update("likes", updatedLikes)
						.await()
					Log.d("CommunityViewModel", "Post likes updated in public feed")
				} catch (e: Exception) {
					Log.d("CommunityViewModel", "Error updating post likes in public feed: $e")

					}
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
