package com.example.culinar.models.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinar.models.Community
import com.example.culinar.models.Post
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CommunityViewModel : ViewModel() {

	private val db = Firebase.firestore

	private val _userId = MutableStateFlow("")
	val userId: StateFlow<String> get() = _userId

	var allCommunities: MutableStateFlow<List<Community>> = MutableStateFlow(emptyList())

	private val _myCommunity = MutableStateFlow<Community?>(null)
	val myCommunity: StateFlow<Community?> get() = _myCommunity

	private val _selectedCommunity = MutableStateFlow<Community?>(null)
	val selectedCommunity: StateFlow<Community?> get() = _selectedCommunity

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
	// Définit l'ID de l'utilisateur actuel
	fun setUserId(id: String) {
		_userId.value = id
	}
	// Rafraîchit les données des communautés (toutes, personnelle, et adhésions)
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
	// Sélectionne une communauté et récupère ses posts
	fun selectCommunity(community: Community) {
		_selectedCommunity.value = community
		Log.d("CommunityViewModel", "Community selected: ${community.id}. Fetching posts.")
		getPosts(community.id)
	}
	// Récupère toutes les communautés depuis Firestore
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
	// Récupère la communauté personnelle de l'expert (s'il en a une)
	fun getMyCommunity() {
		viewModelScope.launch {
			try {
				val result = db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(_userId.value)
					.get()
					.await()
				val community = result.toObject(Community::class.java)
				community?.id = _userId.value
				_myCommunity.value = community
				Log.d("CommunityViewModel", "My community loaded")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error getting my community: $e")
				_myCommunity.value = null
			}
		}
	}
	// Ajoute une nouvelle communauté (créée par l'utilisateur)
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
	// Récupère les membres d'une communauté
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
	// Vérifie si l'utilisateur est membre d'une communauté
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
	// Ajoute un utilisateur comme membre d'une communauté
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
	// Supprime un utilisateur de la liste des membres d'une communauté
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
	// Récupère les posts d'une communauté depuis Firestore
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
				}.sortedByDescending { it.date }

				allPosts.value = fetchedPosts
				Log.d("CommunityViewModel", "Posts loaded for $communityId, count: ${fetchedPosts.size}")
			} catch (e: Exception) {
				Log.e("CommunityViewModel", "Error getting posts for $communityId: $e")
				allPosts.value = emptyList()
			}
		}
	}
	// Vérifie si l'utilisateur a aimé un post
	fun hasLiked(post: Post): Boolean {
		return post.likes.contains(_userId.value)
	}
	// Crée un nouveau post dans la communauté personnelle (si définie)
	fun createPost(post: Post, communityId: String = myCommunity.value?.id ?: "") {
		if (communityId.isEmpty()) {
			Log.e("CommunityViewModel", "Cannot create post: communityId is empty")
			return
		}
		post.authorId = _userId.value
		post.communityId = communityId

		viewModelScope.launch {
			post.username = db.collection(USER_FIREBASE_COLLECTION).document(_userId.value).get().await().get("username").toString()

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

				if (!post.isPrivate) {
					try {
						db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
							.document(postRef.id).set(post.toMap())
							.await()
						Log.d("CommunityViewModel", "Post added to public feed")
					} catch (e: Exception) {
						Log.d("CommunityViewModel", "Error adding post to public feed: $e")
					}
				}

			} catch (e: Exception) {
				Log.d("CommunityViewModel", "Error creating post in $communityId: $e")
			}
		}
	}
	// Ajoute un like à un post donné pour un utilisateur
	fun likePost(post: Post, userId: String) {
		viewModelScope.launch {
			try {
				val communityId = selectedCommunity.value?.id ?: return@launch
				val postRef = db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(communityId)
					.collection("posts")
					.document(post.id)

				val updatedLikes = post.likes.toMutableList().apply {
					if (!contains(userId)) add(userId)
				}

				postRef.update("likes", updatedLikes).await()
				updateLocalPostLikes(post.id, updatedLikes)
				Log.d("CommunityViewModel", "Post liked by $userId")

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

			} catch (e: Exception) {
				Log.d("CommunityViewModel", "Error liking post: $e")
			}
		}
	}
	// Retire un like d'un post donné pour un utilisateur
	fun unlikePost(post: Post, userId: String) {
		viewModelScope.launch {
			try {
				val communityId = selectedCommunity.value?.id ?: return@launch
				val postRef = db.collection(COMMUNITY_FIREBASE_COLLECTION)
					.document(communityId)
					.collection("posts")
					.document(post.id)

				val updatedLikes = post.likes.toMutableList().apply {
					remove(userId)
				}

				postRef.update("likes", updatedLikes).await()
				updateLocalPostLikes(post.id, updatedLikes)
				Log.d("CommunityViewModel", "Post unliked by $userId")

				if (!post.isPrivate) {
					try {
						db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
							.document(post.id)
							.update("likes", updatedLikes)
							.await()
						Log.d("CommunityViewModel", "Post likes updated in pubselic feed")
					} catch (e: Exception) {
						Log.d("CommunityViewModel", "Error updating post likes in public feed: $e")
					}
				}

			} catch (e: Exception) {
				Log.d("CommunityViewModel", "Error unliking post: $e")
			}
		}
	}
	// Met à jour localement la liste des likes d'un post
	private fun updateLocalPostLikes(postId: String, updatedLikes: List<String>) {
		val updatedPosts = allPosts.value.map {
			if (it.id == postId) it.copy(likes = updatedLikes.toMutableList()) else it
		}
		allPosts.value = updatedPosts
	}
}
