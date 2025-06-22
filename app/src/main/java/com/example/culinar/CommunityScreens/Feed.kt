package com.example.culinar.CommunityScreens

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
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
import com.example.culinar.ui.theme.lightGrey
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.example.culinar.R


/**
 * Composable displaying the post feed for the currently selected community.
 *
 * Shows a toolbar with a back button and the selected community's information.
 * If a community is selected, displays its posts via PostFeed composable;
 * otherwise, shows a message indicating no community is selected.
 *
 * @param goBack Callback invoked when the user wants to navigate back.
 * @param communityViewModel ViewModel managing community data and selected community state.
 * @param goToPost Optional callback to navigate to a specific post (currently unused).
 */
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
			Text(
				text = "No community selected",
				modifier = Modifier.padding(16.dp)
			)
		}
	}
}



/**
 * Toolbar composable displaying a back button, a title showing the community name (or default),
 * and optionally a create post button.
 *
 * @param goBack Callback invoked when the back button is pressed.
 * @param community Optional Community whose name is displayed in the toolbar title.
 * @param createPost Optional callback invoked when the create post button is pressed;
 *                   if null, the create post button is hidden.
 */
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
				Icons.AutoMirrored.Filled.KeyboardArrowLeft,
				contentDescription = "Back",
				tint = Color.White,
				modifier = Modifier.size(36.dp)
			)
		}

		Spacer(Modifier.weight(.1f))

		Text(
			text = community?.name ?: "Communaut\u00e9",
			fontSize = 20.sp,
			fontFamily = FontFamily.Serif,
			fontWeight = FontWeight.Bold,
			color = Color.White,
			modifier = Modifier.weight(1f),
			textAlign = TextAlign.Center
		)

		Spacer(Modifier.weight(.2f))

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

/**
 * Toolbar composable specifically for the general feed screen.
 *
 * Displays a back button and a centered title from string resources.
 *
 * @param goBack Callback invoked when the back button is pressed.
 */
@Composable
fun ToolBarGeneralFeed(
	goBack: () -> Unit,
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
				Icons.AutoMirrored.Filled.KeyboardArrowLeft,
				contentDescription = "Back",
				tint = Color.White,
				modifier = Modifier.size(36.dp)
			)
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



/**
 * Composable displaying a scrollable list of posts for the selected community.
 * Supports pull-to-refresh functionality to reload the posts from the ViewModel.
 *
 * @param communityViewModel ViewModel that provides posts data and handles fetching posts for the selected community.
 */
@Composable
fun PostFeed(
	communityViewModel: CommunityViewModel = viewModel()
) {
	// Collect the list of posts as a state that updates when posts change
	val posts by communityViewModel.allPosts.collectAsState()

	// State to track whether the feed is currently refreshing
	var isRefreshing by remember { mutableStateOf(false) }

	// Coroutine scope for launching asynchronous refresh calls
	val coroutineScope = rememberCoroutineScope()

	// Get the currently selected community from the ViewModel
	val selectedCommunity = communityViewModel.selectedCommunity.collectAsState().value

	// Function to refresh posts by invoking ViewModel's getPosts for the selected community
	fun refresh() {
		isRefreshing = true
		coroutineScope.launch {
			selectedCommunity?.id?.let {
				communityViewModel.getPosts(it)
			}
			isRefreshing = false
		}
	}

	// SwipeRefresh composable provides pull-to-refresh UI and triggers the refresh function on swipe
	SwipeRefresh(
		state = rememberSwipeRefreshState(isRefreshing),
		onRefresh = { refresh() },
		modifier = Modifier.padding(10.dp)
	) {
		// LazyColumn efficiently renders the list of posts
		LazyColumn {
			items(posts) { post ->
				PostCard(post = post, communityViewModel = communityViewModel)
				Spacer(modifier = Modifier.height(10.dp))
			}
		}
	}
}



/**
 * Composable that displays a single post card with post details, likes, and comments.
 *
 * Shows the post image, name, content (collapsible), date, like button with count,
 * and expandable comments section with live updates from Firestore.
 *
 * @param post The Post object containing data to display.
 * @param communityViewModel ViewModel to manage likes, comments, and user info related to the post.
 */
@Composable
fun PostCard(post: Post, communityViewModel: CommunityViewModel = viewModel()) {
	// State tracking whether post content is expanded to show full text or truncated
	var expanded by rememberSaveable { mutableStateOf(false) }
	// State controlling visibility of the comments section
	var showComments by rememberSaveable { mutableStateOf(false) }

	// Collect user ID from ViewModel as state
	val userId = communityViewModel.userId.collectAsState().value
	// Check if current user has liked this post
	val liked = communityViewModel.hasLiked(post)

	// Firebase Firestore instance and context for DB operations
	val db = FirebaseFirestore.getInstance()
	val context = LocalContext.current

	// Coroutine scope for async Firestore calls
	val coroutineScope = rememberCoroutineScope()

	// Mutable list state to hold the comments fetched from Firestore in real-time
	val comments = remember { mutableStateListOf<Comment>() }
	// State for new comment input text
	var newComment by remember { mutableStateOf("") }

	// Listener registration to manage Firestore real-time updates; null if no listener active
	var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }

	// Setup Firestore real-time listener for comments when comments are visible and no listener exists
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
							// Fetch username of the comment author from user collection
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
		// Post image loaded asynchronously from URI
		AsyncImage(
			model = post.imageUri,
			contentDescription = "Post image",
			modifier = Modifier
				.fillMaxWidth()
				.height(200.dp)
		)

		Spacer(modifier = Modifier.height(8.dp))

		// Row with like icon and like count
		Row(verticalAlignment = Alignment.CenterVertically) {
			Icon(
				imageVector = if (liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
				contentDescription = "Like",
				tint = if (liked) Color.Red else Color.Gray,
				modifier = Modifier
					.size(30.dp)
					.clickable {
						// Toggle like state on click by calling ViewModel functions
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

		// Post title/name displayed in bold
		Text(text = post.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

		Spacer(modifier = Modifier.height(4.dp))

		// Button toggling expanded/truncated post content with animation on size change
		TextButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
			Text(
				text = if (expanded) post.content else post.content.take(post.content.length / 2) + "...",
				style = MaterialTheme.typography.bodySmall,
				modifier = Modifier.animateContentSize()
			)
		}

		Spacer(modifier = Modifier.height(4.dp))

		// Post date in lighter style and gray color
		Text(text = post.date.toString(), fontSize = 12.sp, fontWeight = FontWeight.Light, color = Color.Gray)

		Spacer(modifier = Modifier.height(8.dp))

		// Button to toggle visibility of comments section
		TextButton(onClick = { showComments = !showComments }) {
			Text(if (showComments) "Masquer les commentaires" else "Voir les commentaires")
		}

		// Comments section shown only if toggled
		if (showComments) {
			Spacer(modifier = Modifier.height(8.dp))

			// List all comments using CommentPost composable
			comments.forEach { CommentPost(it) }

			Spacer(modifier = Modifier.height(8.dp))

			// Row containing new comment input field and send button
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
					// On send, add comment to Firestore and clear input if non-blank
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


/**
 * Composable that displays a single comment with author, content, and timestamp.
 *
 * @param comment The Comment data object containing username, content, and optional timestamp.
 */
@Composable
fun CommentPost(comment: Comment) {
	Column(modifier = Modifier.padding(vertical = 4.dp)) {
		// Display the username in bold with primary color
		Text(
			text = comment.username,
			style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
			color = MaterialTheme.colorScheme.primary
		)
		// Display the comment content in normal body style
		Text(text = comment.content, style = MaterialTheme.typography.bodyMedium)

		// If a timestamp exists, format and display it in smaller gray text
		comment.timestamp?.let {
			val formatted = SimpleDateFormat("dd MMM yyyy à HH:mm", Locale.getDefault()).format(it)
			Text(
				text = formatted,
				style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
			)
		}
	}
}
