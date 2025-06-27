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

/**
 * ViewModel responsible for handling general (public) posts logic:
 * - Fetching all posts from Firestore
 * - Enriching posts with usernames
 * - Creating, liking, and unliking posts
 */
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
		getAllPosts() // Load posts immediately when ViewModel is created
	}

	/**
	 * Set the current user ID for post creation and identification
	 * @param id The user ID to associate with actions in this ViewModel
	 */
	fun setUserId(id: String) {
		_userId.value = id
	}

	/**
	 * Retrieves all public posts from Firestore, orders them by date,
	 * and enriches them with usernames based on authorId.
	 */
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

				// Récupère tous les usernames associés aux userId des posts
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

				// Inject usernames into the Post objects
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

	/**
	 * Adds a new post to Firestore and updates local state immediately
	 * @param post The Post object to be added
	 */
	fun createPost(post: Post) {
		post.authorId = _userId.value

		viewModelScope.launch {
			// Get and assign username from Firestore for preview
			post.username = db.collection(USERS_FIREBASE_COLLECTION)
				.document(_userId.value)
				.get().await()
				.get("username").toString()

			_allPosts.value = listOf(post) + _allPosts.value // Preview the post instantly

			try {
				db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
					.add(post)
					.await()
				Log.d("GeneralPostViewModel", "Post ajouté avec succès")
				getAllPosts() // Refresh post list from Firestore
			} catch (e: Exception) {
				Log.e("GeneralPostViewModel", "Erreur lors de l'ajout du post", e)
				_allPosts.value = _allPosts.value.filter { it != post } // Remove failed post
			}
		}
	}

	/**
	 * Adds the given user's like to the post and syncs it in both public and community feeds
	 * @param post The post to like
	 * @param userId The user ID performing the like
	 */
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

				// Update community version of the post too, if applicable
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

	/**
	 * Removes the given user's like from the post and syncs it in both public and community feeds
	 * @param post The post to unlike
	 * @param userId The user ID performing the unlike
	 */
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

	/**
	 * Updates the like count of a specific post in local state
	 * @param postId The ID of the post to update
	 * @param updatedLikes New list of user IDs who liked the post
	 */
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
