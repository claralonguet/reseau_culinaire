package com.example.culinar.models.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinar.models.Post
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel responsible for managing and exposing the general post feed data.
 * Interacts with Firestore to fetch, add, like, and unlike posts.
 * Maintains UI state like loading and error for UI feedback.
 */
class GeneralPostViewModel : ViewModel() {

	private val db = Firebase.firestore

	// User ID exposed as read-only StateFlow
	private val _userId = MutableStateFlow("")
	val userId: StateFlow<String> get() = _userId

	// All posts exposed as StateFlow
	private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
	val allPosts: StateFlow<List<Post>> = _allPosts

	// Loading state for async operations
	private val _loading = MutableStateFlow(false)
	val loading: StateFlow<Boolean> get() = _loading

	// Error message emitted on failure
	private val _error = MutableStateFlow<String?>(null)
	val error: StateFlow<String?> get() = _error

	init {
		getAllPosts() // Fetch posts initially
	}

	/**
	 * Sets the current user ID.
	 * @param id The user ID to assign.
	 */
	fun setUserId(id: String) {
		_userId.value = id
	}

	/**
	 * Retrieves all general posts from Firestore collection.
	 * Updates local post list and handles error/loading state.
	 */
	fun getAllPosts() {
		viewModelScope.launch {
			_loading.value = true
			try {
				val result = db.collection(GENERAL_POSTS_FIREBASE_COLLECTION).get().await()
				// Map Firestore documents to Post objects
				val posts = result.map {
					it.toObject(Post::class.java).apply { id = it.id }
				}.sortedByDescending { it.date } // Sort posts by most recent
				_allPosts.value = posts
			} catch (e: Exception) {
				_error.value = e.message
				Log.e("GeneralPostViewModel", "Failed to fetch posts", e)
			} finally {
				_loading.value = false
			}
		}
	}

	/**
	 * Adds a new post to the general Firestore post collection.
	 * Automatically assigns current user ID to the post.
	 * Immediately adds it locally and refreshes from backend after success.
	 * Rolls back in case of failure.
	 *
	 * @param post The Post object to add.
	 */
	fun createPost(post: Post) {

		// Add post locally to UI before confirmation
		_allPosts.value += post
		post.authorId = _userId.value

		viewModelScope.launch {
			db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
				.add(post)
				.addOnSuccessListener {
					Log.d("GeneralPostViewModel", "Post added successfully")
				}
				.addOnFailureListener { exception ->
					Log.d("GeneralPostViewModel", "Error adding post", exception)
					_allPosts.value -= post // Rollback on failure
				}
				.await()

			getAllPosts() // Refresh posts to reflect server state
		}
	}

	/**
	 * Adds a like to the given post from the given user ID.
	 * Updates the post in both the community-specific subcollection and public feed if applicable.
	 *
	 * @param post The Post being liked.
	 * @param userId The ID of the user performing the like.
	 */
	fun likePost(post: Post, userId: String) {
		viewModelScope.launch {
			if (post.communityId.isNotEmpty()) {
				try {
					val postRef = db.collection(COMMUNITY_FIREBASE_COLLECTION)
						.document(post.communityId)
						.collection("posts")
						.document(post.id)

					// Create updated list of likes
					val updatedLikes = post.likes.toMutableList().apply {
						if (!contains(userId)) add(userId)
					}

					postRef.update("likes", updatedLikes).await()
					updateLocalPostLikes(post.id, updatedLikes)
					Log.d("CommunityViewModel", "Post liked by $userId")

					// Update public version if exists
					try {
						db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
							.document(post.id)
							.update("likes", updatedLikes)
							.await()
						Log.d("CommunityViewModel", "Post likes updated in public feed")
					} catch (e: Exception) {
						Log.d("CommunityViewModel", "Error updating public feed likes: $e")
					}
				} catch (e: Exception) {
					Log.d("CommunityViewModel", "Error liking post: $e")
				}
			}
		}
	}

	/**
	 * Removes a like from a post for a given user.
	 * Synchronizes the update with the Firestore backend.
	 *
	 * @param post The Post being unliked.
	 * @param userId The ID of the user removing the like.
	 */
	fun unlikePost(post: Post, userId: String) {
		viewModelScope.launch {
			if (post.communityId.isNotEmpty()) {
				try {
					val postRef = db.collection(COMMUNITY_FIREBASE_COLLECTION)
						.document(post.communityId)
						.collection("posts")
						.document(post.id)

					// Remove like from the list
					val updatedLikes = post.likes.toMutableList().apply {
						remove(userId)
					}

					postRef.update("likes", updatedLikes).await()
					updateLocalPostLikes(post.id, updatedLikes)
					Log.d("CommunityViewModel", "Post unliked by $userId")

					// Update public version if post is not private
					if (!post.isPrivate) {
						try {
							db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
								.document(post.id)
								.update("likes", updatedLikes)
								.await()
							Log.d("CommunityViewModel", "Post likes updated in public feed")
						} catch (e: Exception) {
							Log.e("CommunityViewModel", "Error updating public feed likes: $e")
						}
					}
				} catch (e: Exception) {
					Log.d("CommunityViewModel", "Error unliking post: $e")
				}
			}
		}
	}

	/**
	 * Locally updates the post list by replacing the likes for a specific post.
	 *
	 * @param postId ID of the post to update.
	 * @param updatedLikes The new list of user IDs who liked the post.
	 */
	private fun updateLocalPostLikes(postId: String, updatedLikes: List<String>) {
		val updatedPosts = allPosts.value.map {
			if (it.id == postId) it.copy(likes = updatedLikes.toMutableList()) else it
		}
		_allPosts.value = updatedPosts
	}
}
