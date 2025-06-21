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




class GeneralPostViewModel: ViewModel()  {

	private val db = Firebase.firestore

	// userId exposé en lecture seule (StateFlow)
	private val _userId = MutableStateFlow("")
	val userId: StateFlow<String> get() = _userId

	private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
	val allPosts: StateFlow<List<Post>> = _allPosts

	init {
		getAllPosts()
	}

	// --- Met à jour l'ID de l'utilisateur ---
	fun setUserId(id: String) {
		_userId.value = id
	}

	// --- Récupère tous les posts ---
	fun getAllPosts() {

		viewModelScope.launch {
			var posts = mutableListOf<Post>()
			db.collection("Posts")
				.get()
				.addOnSuccessListener { result ->
					result.toObjects(Post::class.java)
					result.forEach {
						posts.add(it.toObject(Post::class.java))
						posts.last().id = it.id

					}
					posts.sortByDescending { it.date }

					_allPosts.value = posts
					Log.d("GeneralPostViewModel", "Posts fetched successfully")
				}
				.addOnFailureListener { exception ->
					Log.d("GeneralPostViewModel", "There was an error getting posts", exception)
				}
				.await()
		}

	}

	// --- Ajoute un post ---
	fun createPost(post: Post) {
		_allPosts.value += post

		post.authorId = _userId.value
		viewModelScope.launch {
			db.collection("Posts")
				.add(post)
				.addOnSuccessListener {
					Log.d("GeneralPostViewModel", "Post added successfully")
				}
				.addOnFailureListener { exception ->
					Log.d("GeneralPostViewModel", "There was an error adding post", exception)
					_allPosts.value -= post
				}
				.await()

			// Met à jour les posts après l'ajout
			getAllPosts()

		}
	}

	// --- Nouvelle fonction : ajoute un like au post ---
	fun likePost(post: Post, userId: String) {

		viewModelScope.launch {

			// Update likes of the community post
			if (post.communityId.isNotEmpty()) {
				try {
					val postRef = db.collection("Communauté")
						.document(post.communityId)
						.collection("posts")
						.document(post.id)

					val updatedLikes = (post.likes.toMutableList()).apply {
						if (!contains(userId)) add(userId)
					}

					postRef.update("likes", updatedLikes).await()
					// Met à jour localement le post dans allPosts
					updateLocalPostLikes(post.id, updatedLikes)
					Log.d("CommunityViewModel", "Post liked by $userId")

					// Update likes of the public version (on the public feed)
					try {
						db.collection("Posts")
							.document(post.id)
							.update("likes", updatedLikes)
							.await()
						Log.d("CommunityViewModel", "Post likes updated in public feed")
					} catch (e: Exception) {
						Log.d("CommunityViewModel", "Error updating post likes in public feed: $e")

					}

				} catch (e: Exception) {
					Log.d("CommunityViewModel", "Error liking post: $e")
				}
			}

		}
	}

	// --- Nouvelle fonction : enlève un like au post ---
	fun unlikePost(post: Post, userId: String) {

		viewModelScope.launch {

			// Update likes of the community post
			if (post.communityId.isNotEmpty()) {
				try {
					val postRef = db.collection("Communauté")
						.document(post.communityId)
						.collection("posts")
						.document(post.id)

					val updatedLikes = (post.likes.toMutableList()).apply {
						remove(userId)
					}

					postRef.update("likes", updatedLikes).await()
					// Met à jour localement le post dans allPosts
					updateLocalPostLikes(post.id, updatedLikes)
					Log.d("CommunityViewModel", "Post unliked by $userId")

					// Update likes of the public version (on the public feed)
					if (!post.isPrivate) {
						try {
							db.collection("Posts")
								.document(post.id)
								.update("likes", updatedLikes)
								.await()
							Log.d("CommunityViewModel", "Post likes updated in public feed")
						} catch (e: Exception) {
							Log.e("CommunityViewModel", "Error updating post likes in public feed: $e")

						}
					}

				} catch (e: Exception) {
					Log.d("CommunityViewModel", "Error unliking post: $e")
				}
			}

		}

	}


	// Met à jour localement le nombre de likes pour un post donné dans allPosts
	private fun updateLocalPostLikes(postId: String, updatedLikes: List<String>) {
		val updatedPosts = allPosts.value.map {
			if (it.id == postId) it.copy(likes = updatedLikes.toMutableList()) else it
		}
		_allPosts.value = updatedPosts
	}


}