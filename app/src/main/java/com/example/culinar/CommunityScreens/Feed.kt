package com.example.culinar.CommunityScreens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.culinar.models.Community
import com.example.culinar.models.Post
import com.example.culinar.models.viewModels.CommunityViewModel
import androidx.compose.material.icons.filled.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


private val lightGrey = Color(0xFFF2F2F2)

@Composable
fun Feed(
	goBack: () -> Unit,
	communityViewModel: CommunityViewModel = viewModel(),
	goToPost: () -> Unit = {}
) {
	val selectedCommunity = communityViewModel.selectedCommunity

	Column {
		ToolBar(goBack = goBack, community = selectedCommunity)
		Spacer(Modifier.height(10.dp))
		if (selectedCommunity != null) {
			PostFeed(communityViewModel = communityViewModel)
		} else {
			Text(text = "No community selected", modifier = Modifier.padding(16.dp))
		}
	}
}

@Composable
fun ToolBar(
	goBack: () -> Unit,
	community: Community?,
	createPost: (() -> Unit)? = null
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.height(80.dp)
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.primary)
			.padding(horizontal = 8.dp)
	) {
		TextButton(
			onClick = goBack,
			shape = CutCornerShape(3.dp),
			colors = ButtonDefaults.textButtonColors(
				containerColor = Color.Transparent,
				contentColor = Color.White
			)
		) {
			Icon(
				Icons.Default.KeyboardArrowLeft,
				contentDescription = "Back",
				tint = Color.White,
				modifier = Modifier.size(36.dp)
			)
		}

		Spacer(Modifier.width(16.dp))

		Text(
			text = community?.name ?: "Communaut\u00e9",
			fontSize = 20.sp,
			fontFamily = FontFamily.Serif,
			fontWeight = FontWeight.Bold,
			color = Color.White,
			modifier = Modifier.weight(1f),
			textAlign = TextAlign.Center
		)

		if (createPost != null) {
			TextButton(
				onClick = createPost,
				shape = CutCornerShape(3.dp),
				colors = ButtonDefaults.textButtonColors(
					containerColor = Color.Transparent,
					contentColor = Color.White
				)
			) {
				Icon(
					Icons.Default.Add,
					contentDescription = "Create Post",
					tint = Color.White,
					modifier = Modifier.size(36.dp)
				)
			}
		}
	}
}

@Composable
fun PostFeed(
	communityViewModel: CommunityViewModel = viewModel()
) {
	val posts by communityViewModel.allPosts.collectAsState()
	var isRefreshing by remember { mutableStateOf(false) }
	val coroutineScope = rememberCoroutineScope()

	// Fonction de rafraîchissement
	fun refresh() {
		isRefreshing = true
		coroutineScope.launch {
			communityViewModel.selectedCommunity?.id?.let { communityViewModel.getPosts(it) }
			isRefreshing = false
		}
	}

	SwipeRefresh(
		state = rememberSwipeRefreshState(isRefreshing),
		onRefresh = { refresh() },
		modifier = Modifier.padding(10.dp)
	) {
		LazyColumn {
			items(posts) { post ->
				PostCard(post = post, communityViewModel = communityViewModel)
				Spacer(modifier = Modifier.height(10.dp))
			}
		}
	}
}

@Composable
fun PostCard(post: Post, communityViewModel: CommunityViewModel = viewModel()) {
	var expanded by rememberSaveable { mutableStateOf(false) }
	var showComments by rememberSaveable { mutableStateOf(false) }
	val userId = communityViewModel.userId.collectAsState().value
	val liked = communityViewModel.hasLiked(post)

	val db = FirebaseFirestore.getInstance()
	val context = LocalContext.current
	val coroutineScope = rememberCoroutineScope()
	val comments = remember { mutableStateListOf<Comment>() }
	var newComment by remember { mutableStateOf("") }
	var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

	if (showComments && listenerRegistration == null) {
		listenerRegistration = db.collection("Post").document(post.id)
			.collection("Comments")
			.orderBy("timestamp", Query.Direction.ASCENDING)
			.addSnapshotListener { snapshot, error ->
				if (error == null && snapshot != null) {
					coroutineScope.launch {
						val tempComments = mutableListOf<Comment>()
						for (doc in snapshot.documents) {
							val id = doc.id
							val idAuthor = doc.getString("idAuthor") ?: continue
							val content = doc.getString("content") ?: ""
							val timestamp = doc.getTimestamp("timestamp")?.toDate()
							val userDoc = db.collection("Utilisateur").document(idAuthor).get().await()
							val username = userDoc.getString("username") ?: "Utilisateur inconnu"
							tempComments.add(Comment(id, idAuthor, content, timestamp, username))
						}
						comments.clear()
						comments.addAll(tempComments)
					}
				}
			}
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(lightGrey)
			.padding(16.dp)
	) {
		AsyncImage(
			model = post.imageUri,
			contentDescription = "Post image",
			modifier = Modifier
				.fillMaxWidth()
				.height(200.dp)
		)

		Spacer(modifier = Modifier.height(8.dp))

		Row(verticalAlignment = Alignment.CenterVertically) {
			Icon(
				imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
				contentDescription = "Like",
				tint = if (liked) Color.Red else Color.Gray,
				modifier = Modifier
					.size(30.dp)
					.clickable {
						if (liked) communityViewModel.unlikePost(post, userId)
						else communityViewModel.likePost(post, userId)
					}
			)
			Spacer(modifier = Modifier.width(8.dp))
			Text(
				text = post.likes.size.toString(),
				fontSize = 18.sp,
				fontWeight = FontWeight.Bold,
				color = Color.DarkGray
			)
		}

		Spacer(modifier = Modifier.height(8.dp))
		Text(text = post.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
		Spacer(modifier = Modifier.height(4.dp))
		TextButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
			Text(
				text = if (expanded) post.content else post.content.take(post.content.length / 2) + "...",
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.animateContentSize()
			)
		}
		Spacer(modifier = Modifier.height(4.dp))
		Text(text = post.date.toString(), fontSize = 12.sp, fontWeight = FontWeight.Light, color = Color.Gray)
		Spacer(modifier = Modifier.height(8.dp))

		TextButton(onClick = { showComments = !showComments }) {
			Text(if (showComments) "Masquer les commentaires" else "Voir les commentaires")
		}

		if (showComments) {
			Spacer(modifier = Modifier.height(8.dp))
			comments.forEach { CommentPost(it) }
			Spacer(modifier = Modifier.height(8.dp))

			Row(verticalAlignment = Alignment.CenterVertically) {
				TextField(
					value = newComment,
					onValueChange = { newComment = it },
					placeholder = { Text("Ajouter un commentaire...") },
					modifier = Modifier.weight(1f),
					maxLines = 3
				)
				Spacer(Modifier.width(8.dp))
				Button(onClick = {
					if (newComment.isNotBlank()) {
						val commentData = hashMapOf(
							"idAuthor" to userId,
							"content" to newComment,
							"timestamp" to com.google.firebase.Timestamp.now()
						)
						db.collection("Post").document(post.id)
							.collection("Comments")
							.add(commentData)
							.addOnSuccessListener { newComment = "" }
					}
				}) {
					Text("Envoyer")
				}
			}
		}
	}
}

@Composable
fun CommentPost(comment: Comment) {
	Column(modifier = Modifier.padding(vertical = 4.dp)) {
		Text(
			text = comment.username,
			style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
			color = MaterialTheme.colorScheme.primary
		)
		Text(text = comment.content, style = MaterialTheme.typography.bodyMedium)
		comment.timestamp?.let {
			val formatted = SimpleDateFormat("dd MMM yyyy à HH:mm", Locale.getDefault()).format(it)
			Text(
				text = formatted,
				style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
			)
		}
	}
}
