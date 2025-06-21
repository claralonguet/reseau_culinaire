package com.example.culinar.CommunityScreens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import coil.compose.rememberAsyncImagePainter
import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culinar.models.Post
import com.example.culinar.models.Recipe
import com.example.culinar.models.viewModels.CommunityViewModel
import com.example.culinar.R


/*
	Displays options:
	- Post a standard post (name, content, image)
	- Post a recipe (name, ingredients, steps, image, ...)
 */
@Composable
fun CreatePost(
	communityViewModel: CommunityViewModel,
	createPost : (Post) -> Unit,
	createRecipe : (Recipe) -> Unit,
	goBack : () -> Unit
) {

	var screenOn by rememberSaveable { mutableIntStateOf(0) }

	ToolBar(goBack = { goBack() }, community = communityViewModel.selectedCommunity)
	Column (
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.fillMaxSize()
		) {

		when(screenOn) {
			0 -> TypeOfPost(
				changeScreenOn = { screenOn = it }
			)
			1 -> StandardPost(
				communityViewModel = communityViewModel,
				createPost = createPost,
				goBack = goBack
			)
			2 -> RecipePost(
				communityViewModel = communityViewModel,
				createRecipe = createRecipe,
				goBack = goBack
			)
			else -> TypeOfPost(
				changeScreenOn = { screenOn = it }
			)
		}
	}
}


@Composable
fun TypeOfPost(
	changeScreenOn : (Int) -> Unit
) {

	var selectedOption by rememberSaveable { mutableIntStateOf(1) }

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.fillMaxWidth(0.9f)
			.fillMaxHeight(0.5f)
			.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
			.border(width = 2.dp, color = MaterialTheme.colorScheme.onPrimary, shape = MaterialTheme.shapes.medium)
			.padding(10.dp)
	)
	{
		Text(
			text = stringResource(R.string.create_post_type),
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.onPrimary,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(10.dp)
		)
		Spacer(modifier = Modifier.height(40.dp))

		Column {
			// Radio button for standard posts
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				RadioButton(
					selected = selectedOption == 1,
					onClick = { selectedOption = 1 },
					colors = RadioButtonDefaults.colors(
						selectedColor = MaterialTheme.colorScheme.onPrimary,
						unselectedColor = MaterialTheme.colorScheme.onPrimary
					)
				)
				Column {
					Text(
						text = stringResource(R.string.create_post_standard),
						color = MaterialTheme.colorScheme.onPrimary,
					)
					Text(
						text = stringResource(R.string.create_post_standard_details),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onPrimary,
					)
				}
			}

			Spacer(modifier = Modifier.height(20.dp))

			// Radio button for recipes
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				RadioButton(
					selected = selectedOption == 2,
					onClick = { selectedOption = 2 },
					colors = RadioButtonDefaults.colors(
						selectedColor = MaterialTheme.colorScheme.onPrimary,
						unselectedColor = MaterialTheme.colorScheme.onPrimary
					)
				)
				Column {
					Text(
						text = stringResource(R.string.create_post_recipe),
						color = MaterialTheme.colorScheme.onPrimary,
					)
					Text(
						text = stringResource(R.string.create_post_recipe_details),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.onPrimary,
					)
				}
			}
		}

		Spacer(modifier = Modifier.height(40.dp))

		TextButton(
			onClick = {
				if(selectedOption == 1)
					changeScreenOn(1)
				else
					changeScreenOn(2)
			},
			shape = MaterialTheme.shapes.medium,
			modifier = Modifier.height(50.dp),
			colors = ButtonColors(
				containerColor = MaterialTheme.colorScheme.onPrimary,
				contentColor = MaterialTheme.colorScheme.primary,
				disabledContainerColor = MaterialTheme.colorScheme.primary,
				disabledContentColor = MaterialTheme.colorScheme.onPrimary
			)
		) {
			Text(
				text = stringResource(R.string.create_post_continue_button),
				style = MaterialTheme.typography.labelSmall
			)
		}

	}
}




/*
	Creates a standard post.

 */
@Composable
fun StandardPost(
	communityViewModel: CommunityViewModel,
	createPost: (Post) -> Unit,
	goBack: () -> Unit
) {

	// Field values
	var name by rememberSaveable { mutableStateOf("") }
	var content by rememberSaveable { mutableStateOf("") }
	var private by rememberSaveable { mutableStateOf(false) }

	// State for the selected image URI
	var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) } // Uri type
	val context = LocalContext.current // Not strictly needed for the picker, but good for other context needs

	// Launcher for the modern photo picker
	val photoPickerLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia()
	) { uri: Uri? ->
		imageUri = uri // Update the state when an image is picked
	}

	Column(
		modifier = Modifier
			.padding(16.dp),
		verticalArrangement = Arrangement.Top, // Align content to the top
		horizontalAlignment = Alignment.CenterHorizontally
	) {

		Text(
			text = stringResource(R.string.create_post_title),
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.primary,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(bottom = 20.dp)
		)

		// Form for the post
		Column(
			verticalArrangement = Arrangement.spacedBy(10.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxWidth(0.9f)

		) {

			// Name of the post
			TextField(
				value = name,
				onValueChange = { name = it },
				label = { Text(stringResource(R.string.create_post_name_label)) },
				modifier = Modifier.fillMaxWidth() // Using fillMaxWidth within the constrained parent
			)

			// Content of the post
			TextField(
				value = content,
				onValueChange = { content = it },
				label = { Text(stringResource(R.string.create_post_content_label)) },
				modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp), // Allow content to be multi-line
				maxLines = 5
			)

			// --- Image Picker Section ---
			Spacer(modifier = Modifier.height(10.dp))

			if (imageUri != null) {
				Image(
					painter = rememberAsyncImagePainter(model = imageUri),
					contentDescription = "Selected Image",
					modifier = Modifier
						.size(200.dp)
						.padding(bottom = 8.dp)
						.align(Alignment.CenterHorizontally) // Center the image
				)
			} else {
				Text(
					stringResource(R.string.create_post_image_picking_message),
					modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
				)
			}

			Button(
				onClick = {
					photoPickerLauncher.launch(
						PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
					)
				},
				modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally)
			) {
				Text(stringResource(R.string.create_post_image_picking_button))
			}
			// --- End of Image Picker Section ---

			Spacer(modifier = Modifier.height(20.dp)) // Spacer before the create button

			Row {
				Checkbox(
					checked = private,
					onCheckedChange = { private = it },
					colors = CheckboxDefaults.colors(
						checkedColor = MaterialTheme.colorScheme.primary,
						uncheckedColor = MaterialTheme.colorScheme.onBackground,
						checkmarkColor = MaterialTheme.colorScheme.onPrimary
					)
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text =
						if(private)
							stringResource(R.string.create_post_private_label_2)
						else
							stringResource(R.string.create_post_private_label_1)
					,
					style = MaterialTheme.typography.bodyMedium,
					modifier = Modifier.align(Alignment.CenterVertically)
				)
			}

			Button(
				onClick = {
					// Basic validation
					if (name.isNotBlank() && content.isNotBlank()) {
						val newPost = Post(
							name = name,
							content = content,
							imageUri = imageUri?.toString() ?: "", // Convert Uri to String for now
							communityId = communityViewModel.selectedCommunity?.id ?: "",
							isPrivate = private
							// id = generated by Firestore
							// Current user ID is set inside the viewModel
						)
						createPost(newPost)
						// Navigate back or clear fields after post creation
						goBack()
					} else {
						// Some error to the user (e.g., using a Snackbar)
						// For now, just logged

						Log.d("CreatePost", "Name and content cannot be blank.")
					}
				},
				modifier = Modifier.align(Alignment.CenterHorizontally).width(150.dp)
			) {
				Text(
					text = stringResource(R.string.create_post_button),
					style = MaterialTheme.typography.labelSmall,
					modifier = Modifier
						.padding(vertical = 8.dp)

				)
			}
		}
	}
}



/*
	Creates a standard post.

 */
@Composable
fun RecipePost(
	communityViewModel: CommunityViewModel,
	createRecipe : (Recipe) -> Unit,
	goBack : () -> Unit
) {

}





@Composable
@Preview(showBackground = true)
fun CreatePostPreview() {
	CreatePost(viewModel(), {}, {}, {})
}

@Composable
fun ModernImagePickerScreen() {
	var imageUri by remember { mutableStateOf<Uri?>(null) }
	val context = LocalContext.current

	// Launcher for the modern photo picker
	val photoPickerLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia()
	) { uri: Uri? ->
		imageUri = uri
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		if (imageUri != null) {
			Image(
				painter = rememberAsyncImagePainter(model = imageUri),
				contentDescription = "Selected Image",
				modifier = Modifier
					.size(200.dp)
					.padding(bottom = 16.dp)
			)
			Text("Image URI: $imageUri")
		} else {
			Text("No image selected", modifier = Modifier.padding(bottom = 16.dp))
		}

		Button(onClick = {
			// Launch the photo picker
			photoPickerLauncher.launch(
				PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly) // Specify you only want images
			)
		}) {
			Text("Select Image (Modern)")
		}

		// To pick from images AND video:
		// Button(onClick = {
		//     photoPickerLauncher.launch(
		//         PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
		//     )
		// }) {
		//     Text("Select Image or Video (Modern)")
		// }
	}
}