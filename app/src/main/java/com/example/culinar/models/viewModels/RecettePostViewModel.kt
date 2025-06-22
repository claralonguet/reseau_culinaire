package com.example.culinar.models.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinar.DataStore.DataStoreManager
import com.example.culinar.models.RecettePost
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class RecettePostViewModel(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _posts = MutableStateFlow<List<RecettePost>>(emptyList())
    val posts: StateFlow<List<RecettePost>> = _posts

    private val _likedPosts = MutableStateFlow<Set<String>>(emptySet())
    val likedPosts: StateFlow<Set<String>> = _likedPosts

    // StateFlow pour stocker l'id utilisateur récupéré de DataStore
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    init {

        viewModelScope.launch {
            dataStoreManager.idFlow.collect {
                _currentUserId.value = it
            }
        }
    }

    fun loadPosts() {
        db.collection("Publications")
            .get()
            .addOnSuccessListener { result ->
                val postList = result.mapNotNull { doc ->
                    val post = doc.toObject<RecettePost>()
                    post.id = doc.id
                    post
                }
                _posts.value = postList
            }
            .addOnFailureListener {
                println("Erreur de récupération des posts : ${it.message}")
            }
    }

    fun toggleLike(postId: String) {
        val currentLikes = _likedPosts.value.toMutableSet()
        if (currentLikes.contains(postId)) {
            currentLikes.remove(postId)
        } else {
            currentLikes.add(postId)
        }
        _likedPosts.value = currentLikes
    }

    fun createPost(content: String, localImageUri: String, onComplete: () -> Unit) {
        val userId = _currentUserId.value
        if (userId.isNullOrEmpty()) {
            // Pas d'utilisateur connecté, on refuse la création
            onComplete()
            return
        }

        val imageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")

        val uploadTask = imageRef.putFile(Uri.parse(localImageUri))
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                val newPost = RecettePost(
                    content = content,
                    imageUri = downloadUri,
                    authorId = userId,
                    date = Date(),
                    likes = listOf(),
                    private = false
                )
                db.collection("Publications")
                    .add(newPost)
                    .addOnSuccessListener { onComplete() }
                    .addOnFailureListener {
                        onComplete()
                    }
            } else {
                onComplete()
            }
        }
    }
}

