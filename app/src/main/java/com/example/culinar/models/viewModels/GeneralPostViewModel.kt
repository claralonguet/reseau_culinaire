package com.example.culinar.models.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinar.models.Post
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GeneralPostViewModel : ViewModel() {

	private val db = Firebase.firestore

	private val _userId = MutableStateFlow("")
	val userId: StateFlow<String> get() = _userId

	private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
	val allPosts: StateFlow<List<Post>> = _allPosts

	private val _loading = MutableStateFlow(false)
	val loading: StateFlow<Boolean> get() = _loading

	private val _error = MutableStateFlow<String?>(null)
	val error: StateFlow<String?> get() = _error

	companion object {
		private const val GENERAL_POSTS_FIREBASE_COLLECTION = "Post"
		private const val COMMUNITY_FIREBASE_COLLECTION = "Communities"
		private const val USERS_FIREBASE_COLLECTION = "users"
	}

	init {
		getAllPosts()
	}

	fun setUserId(id: String) {
		_userId.value = id
	}

	// Nouvelle fonction pour charger les posts ET récupérer les usernames associés
	private fun getAllPosts() {
		viewModelScope.launch {
			_loading.value = true
			try {
				val postsResult = db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
					.orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
					.get()
					.await()

				val rawPosts = postsResult.map {
					it.toObject(Post::class.java).apply { id = it.id }
				}

				if (rawPosts.isEmpty()) {
					_allPosts.value = emptyList()
					_loading.value = false
					return@launch
				}

				val uniqueUserIds = rawPosts.map { it.authorId }.toSet()
				if (uniqueUserIds.isEmpty()) {
					_allPosts.value = rawPosts
					_loading.value = false
					return@launch
				}

				val userMap = mutableMapOf<String, String>()
				val usersRef = db.collection(USERS_FIREBASE_COLLECTION)

				// Lance toutes les requêtes en parallèle et attends leur fin
				uniqueUserIds.map { userId ->
					async {
						try {
							val userDoc = usersRef.document(userId).get().await()
							userMap[userId] = userDoc.getString("username") ?: "Utilisateur inconnu"
						} catch (e: Exception) {
							Log.e("GeneralPostViewModel", "Erreur fetch username for $userId", e)
							userMap[userId] = "Utilisateur inconnu"
						}
					}
				}.awaitAll()

				val enrichedPosts = rawPosts.map { post ->
					post.copy(username = userMap[post.authorId] ?: "Utilisateur inconnu")
				}

				_allPosts.value = enrichedPosts

			} catch (e: Exception) {
				_error.value = e.message
				Log.e("GeneralPostViewModel", "Failed to fetch posts with usernames", e)
			} finally {
				_loading.value = false
			}
		}
	}

	fun createPost(post: Post) {
		post.authorId = _userId.value

		_allPosts.value = listOf(post) + _allPosts.value

		viewModelScope.launch {
			try {
				db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
					.add(post)
					.await()
				Log.d("GeneralPostViewModel", "Post ajouté avec succès")
				getAllPosts()
			} catch (e: Exception) {
				Log.e("GeneralPostViewModel", "Erreur lors de l'ajout du post", e)
				_allPosts.value = _allPosts.value.filter { it != post }
			}
		}
	}

	fun likePost(post: Post, userId: String) {
		viewModelScope.launch {
			try {
				val updatedLikes = post.likes.toMutableList().apply {
					if (!contains(userId)) add(userId)
				}

				val postRef = db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
					.document(post.id)

				postRef.update("likes", updatedLikes).await()

				updateLocalPostLikes(post.id, updatedLikes)

				if (post.communityId.isNotEmpty()) {
					db.collection(COMMUNITY_FIREBASE_COLLECTION)
						.document(post.communityId)
						.collection("posts")
						.document(post.id)
						.update("likes", updatedLikes)
						.await()
				}
			} catch (e: Exception) {
				Log.e("GeneralPostViewModel", "Erreur lors du like", e)
			}
		}
	}

	fun unlikePost(post: Post, userId: String) {
		viewModelScope.launch {
			try {
				val updatedLikes = post.likes.toMutableList().apply {
					remove(userId)
				}

				val postRef = db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
					.document(post.id)

				postRef.update("likes", updatedLikes).await()

				updateLocalPostLikes(post.id, updatedLikes)

				if (post.communityId.isNotEmpty()) {
					db.collection(COMMUNITY_FIREBASE_COLLECTION)
						.document(post.communityId)
						.collection("posts")
						.document(post.id)
						.update("likes", updatedLikes)
						.await()
				}
			} catch (e: Exception) {
				Log.e("GeneralPostViewModel", "Erreur lors du unlike", e)
			}
		}
	}

	private fun updateLocalPostLikes(postId: String, updatedLikes: List<String>) {
		_allPosts.value = _allPosts.value.map {
			if (it.id == postId) it.copy(likes = updatedLikes) else it
		}
	}
}
