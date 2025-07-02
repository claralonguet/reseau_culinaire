package com.example.culinar.CommunityScreens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.culinar.R
import com.example.culinar.models.Community
import com.example.culinar.models.Post
import com.example.culinar.models.viewModels.CommunityViewModel
import com.example.culinar.ui.theme.lightGrey
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.animation.animateContentSize
import com.example.culinar.models.viewModels.COMMUNITY_FIREBASE_COLLECTION
import com.example.culinar.models.viewModels.GENERAL_POSTS_FIREBASE_COLLECTION
import com.example.culinar.models.viewModels.USER_FIREBASE_COLLECTION
import androidx.compose.runtime.collectAsState

// Composable principal affichant le fil d’actualité de la communauté sélectionnée.
// Contient la barre supérieure (ToolBar) et la liste des posts si une communauté est sélectionnée.

@Composable
fun Feed(
	goBack: () -> Unit,
	communityViewModel: CommunityViewModel = viewModel(),
	goToPost: () -> Unit = {}
) {
	val selectedCommunity = communityViewModel.selectedCommunity.collectAsState().value

	Column {
		ToolBar(goBack = goBack, community = selectedCommunity)
		Spacer(Modifier.height(10.dp))
		if (selectedCommunity != null) {
			PostFeed(communityViewModel = communityViewModel)
		} else {
			Text("No community selected", modifier = Modifier.padding(16.dp))
		}
	}
}
// Barre d'outils affichée en haut de l'écran.
// Affiche le bouton retour, le nom de la communauté et éventuellement un bouton pour créer un post.

@Composable
fun ToolBar(goBack: () -> Unit, community: Community?, createPost: (() -> Unit)? = null) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.height(80.dp)
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.primary)
			.padding(horizontal = 8.dp)
	) {
		TextButton(onClick = goBack) {
			Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(36.dp))
		}
		Spacer(Modifier.weight(.1f))
		Text(
			text = community?.name ?: "Communauté",
			fontSize = 20.sp,
			fontFamily = FontFamily.Serif,
			fontWeight = FontWeight.Bold,
			color = Color.White,
			modifier = Modifier.weight(1f),
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.weight(.2f))
		if (createPost != null) {
			TextButton(onClick = createPost) {
				Icon(Icons.Default.Add, contentDescription = "Create Post", tint = Color.White, modifier = Modifier.size(36.dp))
			}
		}
	}
}

@Composable
fun ToolBarGeneralFeed(goBack: () -> Unit) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.height(80.dp)
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.primary)
			.padding(horizontal = 8.dp)
	) {
		TextButton(onClick = goBack) {
			Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(36.dp))
		}
		Spacer(Modifier.weight(.1f))
		Text(
			text = stringResource(R.string.feed_post_screen_title),
			fontSize = 20.sp,
			fontFamily = FontFamily.Serif,
			fontWeight = FontWeight.Bold,
			color = Color.White,
			modifier = Modifier.weight(1f),
			textAlign = TextAlign.Center
		)
		Spacer(Modifier.weight(.2f))
	}
}
// Affiche la liste des posts
// Intègre une fonctionnalité de "pull-to-refresh" via SwipeRefresh.
@Composable
fun PostFeed(communityViewModel: CommunityViewModel = viewModel()) {
	val posts by communityViewModel.allPosts.collectAsState()
	var isRefreshing by remember { mutableStateOf(false) }
	val coroutineScope = rememberCoroutineScope()
	val selectedCommunity = communityViewModel.selectedCommunity.collectAsState().value


	fun refresh() {
		isRefreshing = true
		coroutineScope.launch {
			selectedCommunity?.id?.let { communityViewModel.getPosts(it) }
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
// Carte représentant un post individuel
// Gère les interactions utilisateur comme liker et commenter.
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
	var currentUsername by remember { mutableStateOf<String?>(null) }

	// Récupération de la communauté sélectionnée
	val selectedCommunity = communityViewModel.selectedCommunity.collectAsState().value

	// Récupération du nom de l'utilisateur courant
	LaunchedEffect(userId) {
		coroutineScope.launch {
			currentUsername =
				db.collection("Utilisateur").document(userId).get().await().getString("username")
			Log.d("PostCard", "username: $currentUsername")
		}
	}
// Animation de cœur rouge qui apparaît brièvement lorsque l'utilisateur aime un post.
	var showHeart by remember { mutableStateOf(false) }
	val scale = remember { Animatable(0f) }

	// Listener des commentaires, adapté selon si post privé ou non
	LaunchedEffect(showComments) {
		if (showComments && listenerRegistration == null) {
			val commentsCollection = if (post.isPrivate) {
				val communityId = selectedCommunity?.id
				if (communityId == null) {
					null
				} else {
					db.collection(COMMUNITY_FIREBASE_COLLECTION)
						.document(communityId)
						.collection("posts")
						.document(post.id)
						.collection("Comments")
				}
			} else {
				db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
					.document(post.id)
					.collection("Comments")
			}

			commentsCollection?.let { collection ->
				listenerRegistration = collection
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
									val username = userDoc.getString("username")
										?: db.collection(USER_FIREBASE_COLLECTION).document(idAuthor).get()
											.await().getString("username")
										?: "Utilisateur inconnu"
									tempComments.add(Comment(id, idAuthor, content, timestamp, "", username))
								}
								comments.clear()
								comments.addAll(tempComments)
							}
						}
					}
			}
		} else if (!showComments) {
			listenerRegistration?.remove()
			listenerRegistration = null
			comments.clear()
		}
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.background(lightGrey)
			.padding(16.dp)
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(200.dp),
			contentAlignment = Alignment.Center
		) {
			AsyncImage(
				model = post.imageUri,
				contentDescription = "Post image",
				modifier = Modifier.fillMaxSize()
			)
			androidx.compose.animation.AnimatedVisibility(
				visible = showHeart,
				enter = fadeIn(),
				exit = fadeOut()
			) {
				Icon(
					imageVector = Icons.Filled.Favorite,
					contentDescription = null,
					tint = Color.Red,
					modifier = Modifier
						.scale(scale.value)
						.size(100.dp)
				)
			}
		}

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
						else {
							communityViewModel.likePost(post, userId)
							// Lancement de l'animation de cœur lors du like.
							showHeart = true
							coroutineScope.launch {
								scale.snapTo(0f)
								scale.animateTo(1.5f, tween(200, easing = LinearOutSlowInEasing))
								scale.animateTo(0f, tween(200, easing = FastOutLinearInEasing))
								showHeart = false
							}
						}
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
		Text(
			text = post.name,
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.Bold
		)
		Spacer(modifier = Modifier.height(4.dp))
		TextButton(
			onClick = { expanded = !expanded },
			modifier = Modifier.fillMaxWidth()
		) {
			Text(
				text = if (expanded) post.content else post.content.take(post.content.length / 2) + "...",
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.animateContentSize()
			)
		}
		Spacer(modifier = Modifier.height(4.dp))
		Text(
			text = post.date.toString(),
			fontSize = 12.sp,
			fontWeight = FontWeight.Light,
			color = Color.Gray
		)
		Spacer(modifier = Modifier.height(8.dp))
		// Toggle commentaires
		TextButton(onClick = { showComments = !showComments }) {
			Text(if (showComments) "Masquer les commentaires" else "Voir les commentaires")
		}

		// Affichage + ajout commentaire
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
							"username" to (currentUsername ?: "Utilisateur inconnu"),
							"timestamp" to com.google.firebase.Timestamp.now()
						)

						val communityId = selectedCommunity?.id

						// Commentaire dans la collection privée
						if (post.isPrivate && communityId != null) {
							db.collection(COMMUNITY_FIREBASE_COLLECTION)
								.document(communityId)
								.collection("posts")
								.document(post.id)
								.collection("Comments")
								.add(commentData)
								.addOnSuccessListener { newComment = "" }
								.addOnFailureListener {
									Log.e("PostCard", "Erreur ajout commentaire (privé)", it)
								}
						}

						// Commentaire dans la collection publique
						if (!post.isPrivate) {
							db.collection(GENERAL_POSTS_FIREBASE_COLLECTION)
								.document(post.id)
								.collection("Comments")
								.add(commentData)
								.addOnSuccessListener { newComment = "" }
								.addOnFailureListener {
									Log.e("PostCard", "Erreur ajout commentaire (public)", it)
								}
						}
					}
				}) {
					Text("Envoyer")
				}
			}
		}
	}
}



// Composable affichant un commentaire avec nom de l’auteur, contenu et date.
@Composable
fun CommentPost(comment: Comment) {
	Column(modifier = Modifier.padding(vertical = 4.dp)) {
		Text(text = comment.username, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
		Text(text = comment.content, style = MaterialTheme.typography.bodyMedium)
		comment.timestamp?.let {
			val formatted = SimpleDateFormat("dd MMM yyyy à HH:mm", Locale.getDefault()).format(it)
			Text(text = formatted, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
		}
	}
}
