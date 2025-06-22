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

	private fun getAllPosts() {
		viewModelScope.launch {
			_loading.value = true
			try {
				Log.d("GeneralPostViewModel", "Début récupération des posts...")

				val postsResult = db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
					.orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
					.get()
					.await()

				val rawPosts = postsResult.map {
					it.toObject(Post::class.java).apply { id = it.id }
				}

				Log.d("GeneralPostViewModel", "Posts récupérés: ${rawPosts.size}")
				rawPosts.forEach { Log.d("GeneralPostViewModel", "Post brut: $it") }

				if (rawPosts.isEmpty()) {
					Log.d("GeneralPostViewModel", "Aucun post trouvé.")
					_allPosts.value = emptyList()
					_loading.value = false
					return@launch
				}

				val uniqueUserIds = rawPosts.map { it.authorId }.filter { it.isNotBlank() }.toSet()
				Log.d("GeneralPostViewModel", "IDs utilisateurs uniques: $uniqueUserIds")

				if (uniqueUserIds.isEmpty()) {
					Log.w("GeneralPostViewModel", "Aucun authorId valide trouvé dans les posts.")
					_allPosts.value = rawPosts
					_loading.value = false
					return@launch
				}

				val userMap = mutableMapOf<String, String>()
				val usersRef = db.collection("Utilisateur") // <-- Modifié ici

				uniqueUserIds.map { userId ->
					async {
						try {
							Log.d("GeneralPostViewModel", "Récupération username pour userId: $userId")
							val userDoc = usersRef.document(userId).get().await()
							val username = userDoc.getString("username") ?: "Utilisateur inconnu"
							userMap[userId] = username
							Log.d("GeneralPostViewModel", "Username trouvé pour $userId: $username")
						} catch (e: Exception) {
							Log.e("GeneralPostViewModel", "Erreur récupération username pour $userId", e)
							userMap[userId] = "Utilisateur inconnu"
						}
					}
				}.awaitAll()

				Log.d("GeneralPostViewModel", "Mapping userId -> username complété: $userMap")

				val enrichedPosts = rawPosts.map { post ->
					post.apply {
						username = userMap[authorId] ?: "Utilisateur inconnu"
					}
				}

				Log.d("GeneralPostViewModel", "Posts enrichis avec username:")
				enrichedPosts.forEach { Log.d("GeneralPostViewModel", it.toString()) }

				_allPosts.value = enrichedPosts

			} catch (e: Exception) {
				_error.value = e.message
				Log.e("GeneralPostViewModel", "Erreur lors de la récupération des posts et usernames", e)
			} finally {
				_loading.value = false
				Log.d("GeneralPostViewModel", "Fin de la récupération des posts")
			}
		}
	}







	fun createPost(post: Post) {
		post.authorId = _userId.value


		viewModelScope.launch {
			post.username = db.collection(USERS_FIREBASE_COLLECTION).document(_userId.value).get().await().get("username").toString()

			_allPosts.value = listOf(post) + _allPosts.value

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
		_allPosts.value = _allPosts.value.map { post ->
			if (post.id == postId) {
				Post(
					id = post.id,
					name = post.name,
					content = post.content,
					likes = updatedLikes,
					date = post.date,
					imageUri = post.imageUri,
					authorId = post.authorId,
					isPrivate = post.isPrivate,
					communityId = post.communityId
				).also {
					it.username = post.username  // on conserve le username
				}
			} else {
				post
			}
		}
	}

}
