package com.example.culinar.recipesScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.culinar.recipesScreen.components.PostCard
import com.example.culinar.models.RecettePost
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@Composable
fun PostFeedScreen() {
    var posts by remember { mutableStateOf<List<RecettePost>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        fetchPosts { fetchedPosts ->
            posts = fetchedPosts
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Fil d’actualité",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(posts) { post ->
                    PostCard(post = post)
                }
            }
        }
    }
}
fun fetchPosts(onResult: (List<RecettePost>) -> Unit) {
    FirebaseFirestore.getInstance()
        .collection("Publications")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener { result ->
            val posts = result.documents.mapNotNull { it.toObject(RecettePost::class.java) }
            onResult(posts)
        }
        .addOnFailureListener {
            onResult(emptyList())
        }
}
