package com.example.culinar.recipesScreen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.culinar.models.RecettePost
import com.example.culinar.models.viewModels.RecettePostViewModel
import com.example.culinar.ui.theme.lightGrey

@Composable
fun FeedRecetteScreen(goBack: () -> Unit, viewModel: RecettePostViewModel = viewModel()) {
    Column {
        RecetteToolbar(goBack = goBack)
        Spacer(Modifier.height(10.dp))
        PostFeed(viewModel = viewModel)
    }
}

@Composable
fun RecetteToolbar(goBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        TextButton(onClick = goBack) {
            Text("Retour", color = Color.White)
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = "Publications recettes",
            fontSize = 20.sp,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}

@Composable
fun PostFeed(viewModel: RecettePostViewModel = viewModel()) {
    val posts by viewModel.posts.collectAsState()
    val likedPosts by viewModel.likedPosts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPosts()
    }

    Log.d("FeedRecetteScreen", "Posts: ${posts.map { it.id }}")

    Column(modifier = Modifier.padding(10.dp)) {
        LazyColumn {
            items(posts) { post ->
                PostCard(post = post, hasLiked = likedPosts.contains(post.id)) {
                    viewModel.toggleLike(post.id)
                }
            }
        }

    }
}

@Composable
fun PostCard(post: RecettePost, hasLiked: Boolean, onLikeClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(lightGrey),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = post.imageUri,
            contentDescription = "Image de la recette",
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row {
            TextButton(onClick = onLikeClick) {
                Icon(
                    imageVector = if (hasLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = Color.Red
                )
            }
            Text(
                text = post.likes.size.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Auteur: ${post.authorId}",
            style = MaterialTheme.typography.titleSmall
        )

        TextButton(onClick = { expanded = !expanded }) {
            Text(
                text = if (expanded) post.content else post.content.take(50) + if (post.content.length > 50) "..." else "",
                modifier = Modifier.animateContentSize(),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = post.date.toString(),
            fontSize = 12.sp,
            fontFamily = FontFamily.Serif,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}
