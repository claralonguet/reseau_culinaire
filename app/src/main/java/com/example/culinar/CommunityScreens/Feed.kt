package com.example.culinar.CommunityScreens

import android.util.Log
import android.widget.Toolbar
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.culinar.models.Community
import com.example.culinar.models.Post
import com.example.culinar.models.viewModels.CommunityViewModel
import com.example.culinar.ui.theme.darkGrey
import com.example.culinar.ui.theme.grey
import com.example.culinar.ui.theme.lightGrey


@Composable
fun Feed(goBack : () -> Unit, communityViewModel: CommunityViewModel = viewModel(), goToPost : () -> Unit = {}) {

	val selectedCommunity = communityViewModel.selectedCommunity
	Column {

		ToolBar(goBack = goBack, community = selectedCommunity)
		Spacer(Modifier.height(10.dp))
		if (selectedCommunity != null) {
			PostFeed(communityViewModel = communityViewModel)
		} else {
			Text(text = "No community selected")
		}
	}

}


@Composable
fun ToolBar(goBack: () -> Unit, community: Community?, createPost : (() -> Unit)? = null) {

	// Screen title and options
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.height(80.dp)
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.primary)
	) {
		// Return button
		TextButton(
			onClick = { goBack() },
			shape = CutCornerShape(3.dp),
			colors = ButtonColors(
				containerColor = Color(0x0059EA85),
				contentColor = Color.White,
				disabledContainerColor = Color(0xFF59EA85),
				disabledContentColor = Color.White
			)
		) {
			Icon(
				Icons.AutoMirrored.Default.KeyboardArrowLeft,
				contentDescription = "Cancel",
				tint = lightGrey,
				modifier = Modifier.height(100.dp).width(45.dp)
			)

		}

		Spacer(Modifier.width(45.dp))

		// Title of the subscreen
		Text(
			text = community?.name ?: "Communauté",
			fontSize = 20.sp,
			fontFamily = FontFamily.Serif,
			textAlign = TextAlign.Center,
			fontWeight = FontWeight.Bold,
			lineHeight = 50.sp,
			color = MaterialTheme.colorScheme.onPrimary,

			modifier = Modifier
				.height(50.dp)
		)

		Spacer(Modifier.weight(1f))
		if (createPost != null) {
			TextButton(
				onClick = { createPost() },
				shape = CutCornerShape(3.dp),
				colors = ButtonColors(
					containerColor = Color(0x0059EA85),
					contentColor = Color.White,
					disabledContainerColor = Color(0xFF59EA85),
					disabledContentColor = Color.White
				)
			) {
				Icon(
					Icons.Default.Add,
					contentDescription = "Post something",
					tint = lightGrey,
					modifier = Modifier.height(100.dp).width(45.dp)
				)
			}
		}

	}

}


@Composable
fun PostFeed(communityViewModel: CommunityViewModel = viewModel()) {

	val posts by communityViewModel.allPosts.collectAsState()

Log.d("PostFeedUI", "Recomposing. Posts list instance: ${System.identityHashCode(posts)}. Number of posts: ${posts.size}. Post IDs: ${posts.map { it.id }}")

	Column(
		modifier = Modifier.padding(10.dp)
	) {
		LazyColumn {
			items(posts) { post ->
				PostCard(post = post, communityViewModel = communityViewModel)
			}
		}
	}
}


@Composable
fun PostCard(post: Post, communityViewModel: CommunityViewModel = viewModel()) {

	var expanded by rememberSaveable { mutableStateOf(false) }

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.height(400.dp)
			.background(lightGrey),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		AsyncImage(
			model = post.imageUri,
			contentDescription = "Post image",
			modifier = Modifier
				.fillMaxWidth(0.8f)
				.height(200.dp)
		)
		Spacer(modifier = Modifier.height(10.dp))

		// Like button

		Row {
			TextButton(
				onClick = {/* TODO: Implement liking the post */}
			) {
				Icon(
					if(communityViewModel.hasLiked(post))
						Icons.Default.Favorite
					else
						Icons.Default.FavoriteBorder,
					contentDescription = "Like",
					tint = Color.Red
				)
			}
			//Spacer(modifier = Modifier.width(2.dp))
			Text(
				text = post.likes.size.toString(),
				fontSize = 20.sp,
				fontFamily = FontFamily.Serif,
				textAlign = TextAlign.Center,
				fontWeight = FontWeight.Bold,

			)
		}

		Spacer(modifier = Modifier.height(10.dp))
		// Post name and content
		Text(
			text = post.name,
			fontStyle = MaterialTheme.typography.titleMedium.fontStyle,
			fontWeight = FontWeight.Bold,

		)
		TextButton(
			onClick = {expanded != expanded}
		) {
			Text(
				text = if(expanded) post.content else "${post.content.substring(0, post.content.length/2)}...",
				fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
				modifier = Modifier.animateContentSize()
			)
		}
		Spacer(modifier = Modifier.height(5.dp))
		// Post creation date
		Text(
			text = post.date.toString(),
			fontSize = 12.sp,
			fontFamily = FontFamily.Serif,
			textAlign = TextAlign.Center,
			fontWeight = FontWeight.Bold,
		)

	}
}


@Composable
fun PostDetails(post: Post, goBack: () -> Unit) {

}


@Preview(showBackground = true)
@Composable
fun FeedPreview() {
	Feed(goBack = {})
}