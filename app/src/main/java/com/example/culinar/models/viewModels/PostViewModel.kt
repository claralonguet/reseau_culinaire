package com.example.culinar.models.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culinar.models.Post
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

	private val db = Firebase.firestore
	private val userId : MutableStateFlow<String> = MutableStateFlow<String>("")

	var allPosts: MutableStateFlow<List<Post>> = MutableStateFlow<List<Post>>(listOf())

	init {
		// Load communities when userId changes
		viewModelScope.launch {
			userId.collect {
				// Update communities when userId is not null
				if (it.isNotEmpty())
					refreshPosts()
			}
		}
	}

	// User session info setup
	fun setUserId(id: String) {
		userId.value = id
	}

	// Posts
	fun refreshPosts() {
		getPosts()
	}

	fun getPosts() {

	}

}