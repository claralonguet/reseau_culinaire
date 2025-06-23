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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.culinar.models.viewModels.GeneralPostViewModel


/**
 * Composable that allows the user to create a post within a community.
 *
 * Displays a selection screen to choose the type of post: standard post or recipe.
 * Depending on the choice, navigates to the corresponding form composable.
 *
 * @param communityViewModel ViewModel managing the selected community and posts.
 * @param createPost Callback invoked to create a standard post.
 * @param createRecipe Callback invoked to create a recipe post.
 * @param goBack Callback to navigate back or close the post creation screen.
 */
@Composable
fun CreatePost(
	communityViewModel: CommunityViewModel,
	createPost: (Post) -> Unit,
	createRecipe: (Recipe) -> Unit,
	goBack: () -> Unit
) {
	// Keeps track of the current screen to show:
	// 0 = post type selection, 1 = standard post form, 2 = recipe post form
	var screenOn by rememberSaveable { mutableIntStateOf(0) }

	Column {
		// Displays a toolbar with back button and community info
		ToolBar(
			goBack = { goBack() },
			community = communityViewModel.myCommunity.collectAsState().value
		)

		// Main container column centered both vertically and horizontally
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier
				.fillMaxSize()
		) {
			// Shows the screen according to current selection
			when (screenOn) {
				0 -> TypeOfPost(changeScreenOn = { screenOn = it }) // Choose post type
				1 -> StandardPost(
					communityViewModel = communityViewModel,
					createPost = createPost,
					goBack = goBack
				) // Standard post creation form
				2 -> RecipePost(
					communityViewModel = communityViewModel,
					createRecipe = createRecipe,
					goBack = goBack
				) // Recipe post creation form
				else -> TypeOfPost(changeScreenOn = {
					screenOn = it
				}) // Default to post type selection
			}
		}

	}
}





/**
 * Composable for creating a general feed post in the public feed.
 *
 * Displays the UI for creating a standard post, including fields for name, content, and image.
 * This composable does not offer the recipe post option.
 *
 * @param generalPostViewModel ViewModel managing the state and logic for general posts.
 * @param createPost Callback invoked with the created Post object to be saved or sent.
 * @param goBack Callback to navigate back or close the post creation screen.
 */
@Composable
fun CreateGeneralFeedPost(
	generalPostViewModel: GeneralPostViewModel,
	createPost: (Post) -> Unit,
	goBack: () -> Unit
) {
	// Screen state variable currently unused as only standard post form is shown
	var screenOn by rememberSaveable { mutableIntStateOf(0) }

	Column {
		// Shows a toolbar dedicated for the general feed context with a back button
		ToolBarGeneralFeed(goBack = { goBack() })

		// Container column centered vertically and horizontally, filling available space
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.fillMaxSize()
		) {
			// Directly show the standard post creation form
			StandardPost(
				generalPostViewModel = generalPostViewModel,
				createPost = createPost,
				goBack = goBack
			)
		}
	}
}





/**
 * Composable that displays options for selecting the type of post to create.
 *
 * Shows radio buttons for choosing between a standard post and a recipe post,
 * along with descriptions for each option. Includes a continue button that triggers
 * navigation to the selected post creation screen.
 *
 * @param changeScreenOn Callback invoked with the selected option's screen index:
 *                       1 for standard post, 2 for recipe post.
 */
@Composable
fun TypeOfPost(
	changeScreenOn: (Int) -> Unit
) {
	// Holds the currently selected option: 1 = standard post, 2 = recipe post
	var selectedOption by rememberSaveable { mutableIntStateOf(1) }

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.fillMaxWidth(0.9f)
			.fillMaxHeight(0.5f)
			.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
			.border(width = 2.dp, color = MaterialTheme.colorScheme.onPrimary, shape = MaterialTheme.shapes.medium)
			.padding(10.dp)
	) {
		// Title describing the selection
		Text(
			text = stringResource(R.string.create_post_type),
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.onPrimary,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(10.dp)
		)

		Spacer(modifier = Modifier.height(40.dp))

		Column {
			// Option 1: Standard post selection with radio button and description
			Row(verticalAlignment = Alignment.CenterVertically) {
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

			// Option 2: Recipe post selection with radio button and description
			Row(verticalAlignment = Alignment.CenterVertically) {
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

		// Continue button triggers the navigation callback with the selected option's index
		TextButton(
			onClick = {
				if (selectedOption == 1) changeScreenOn(1) else changeScreenOn(2)
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






/**
 * Composable for creating a standard post with text content, optional image, and privacy setting.
 *
 * @param communityViewModel Optional ViewModel managing community state; used to get the selected community and determine privacy options.
 * @param generalPostViewModel Optional ViewModel for general feed posts (not used directly here).
 * @param createPost Lambda called to create the post; receives a Post object constructed from the input fields.
 * @param goBack Lambda called to navigate back or close the post creation screen after successfully creating a post.
 *
 * The UI includes inputs for post name, multiline content, an image picker, a privacy checkbox (if communityViewModel is provided),
 * and a create button that validates inputs before calling createPost.
 */
@Composable
fun StandardPost(
	communityViewModel: CommunityViewModel? = null,
	generalPostViewModel: GeneralPostViewModel? = null,
	createPost: (Post) -> Unit,
	goBack: () -> Unit
) {
	// Scroll state for the form
	var scrollableForm = rememberScrollState()

	// Field values for the post inputs
	var name by rememberSaveable { mutableStateOf("") }
	var content by rememberSaveable { mutableStateOf("") }
	var private by rememberSaveable { mutableStateOf(true) }
	val selectedCommunityId = communityViewModel?.selectedCommunity?.collectAsState()?.value

	// State for selected image URI
	var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
	val context = LocalContext.current

	// Image picker launcher for selecting images from gallery
	val photoPickerLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia()
	) { uri: Uri? ->
		imageUri = uri
	}

	Column(
		modifier = Modifier
			.padding(16.dp)
			.verticalScroll(scrollableForm),
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = stringResource(R.string.create_post_title),
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.primary,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(bottom = 20.dp)
		)

		Column(
			verticalArrangement = Arrangement.spacedBy(10.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.fillMaxWidth(0.9f)
		) {
			TextField(
				value = name,
				onValueChange = { name = it },
				label = { Text(stringResource(R.string.create_post_name_label)) },
				modifier = Modifier.fillMaxWidth()
			)

			TextField(
				value = content,
				onValueChange = { content = it },
				label = { Text(stringResource(R.string.create_post_content_label)) },
				modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
				maxLines = 5
			)

			Spacer(modifier = Modifier.height(10.dp))

			if (imageUri != null) {
				Image(
					painter = rememberAsyncImagePainter(model = imageUri),
					contentDescription = "Selected Image",
					modifier = Modifier
						.size(200.dp)
						.padding(bottom = 8.dp)
						.align(Alignment.CenterHorizontally)
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

			Spacer(modifier = Modifier.height(20.dp))

			if (communityViewModel != null) {
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
						text = if (private)
							stringResource(R.string.create_post_private_label_1)
						else
							stringResource(R.string.create_post_private_label_2),
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier.align(Alignment.CenterVertically)
					)
				}
			}

			Button(
				onClick = {
					if (name.isNotBlank() && content.isNotBlank()) {
						val newPost = Post(
							name = name,
							content = content,
							imageUri = imageUri?.toString() ?: "",
							communityId = selectedCommunityId?.id ?: "",
							isPrivate = private
						)
						createPost(newPost)
						goBack()
					} else {
						Log.d("CreatePost", "Name and content cannot be blank.")
					}
				},
				modifier = Modifier.align(Alignment.CenterHorizontally).width(150.dp)
			) {
				Text(
					text = stringResource(R.string.create_post_button),
					style = MaterialTheme.typography.labelSmall,
					modifier = Modifier.padding(vertical = 8.dp)
				)
			}
		}
	}
}




/**
 * Composable allowing the user to create a recipe post.
 *
 * @param communityViewModel ViewModel managing community data and state; used here mainly for context like selected community.
 * @param createRecipe Lambda invoked when submitting a new Recipe; receives the created Recipe object.
 * @param goBack Lambda called to navigate back or close the create recipe screen after submission or cancellation.
 *
 * The UI includes inputs for recipe name, multiline ingredients and steps, an image picker, and a privacy toggle.
 */
@Composable
fun RecipePost(
	communityViewModel: CommunityViewModel,
	createRecipe: (Recipe) -> Unit,
	goBack: () -> Unit
) {

	var scrollableForm = rememberScrollState()

	var name by rememberSaveable { mutableStateOf("") }
	var ingredientsText by rememberSaveable { mutableStateOf("") }
	var stepsText by rememberSaveable { mutableStateOf("") }
	var private by rememberSaveable { mutableStateOf(true) }
	var time by rememberSaveable { mutableStateOf("") }
	var difficulty by rememberSaveable { mutableStateOf("facile") }
	var difficultyOptionsExpanded by rememberSaveable { mutableStateOf(false) }

	val selectedCommunityId = communityViewModel.selectedCommunity.collectAsState().value?.id ?: ""

	var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
	val context = LocalContext.current

	val photoPickerLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia()
	) { uri: Uri? ->
		imageUri = uri
	}

	Column(
		modifier = Modifier
			.padding(16.dp)
			.verticalScroll(scrollableForm),
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(
			text = stringResource(R.string.create_recipe_title),
			style = MaterialTheme.typography.titleLarge,
			color = MaterialTheme.colorScheme.primary,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(bottom = 20.dp)
		)

		Column(
			verticalArrangement = Arrangement.spacedBy(10.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.fillMaxWidth(0.9f)
		) {
			TextField(
				value = name,
				onValueChange = { name = it },
				label = { Text(stringResource(R.string.create_recipe_name_label)) },
				modifier = Modifier.fillMaxWidth()
			)

			TextField(
				value = ingredientsText,
				onValueChange = { ingredientsText = it },
				label = { Text(stringResource(R.string.create_recipe_ingredients_label)) },
				modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
				maxLines = 6
			)

			TextField(
				value = stepsText,
				onValueChange = { stepsText = it },
				label = { Text(stringResource(R.string.create_recipe_steps_label)) },
				modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
				maxLines = 8
			)

			// Cooking time picker. Accepting only numbers
			TextField(
				value = time,
				onValueChange = { time = it.filter { char -> char.isDigit() } },
				label = { Text(stringResource(R.string.create_recipe_time_label)) },
			)


			Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
				// Difficulty picker
				TextField(
					value = difficulty,
					onValueChange = { difficulty = it },
					label = { Text(stringResource(R.string.create_recipe_difficulty_label)) },
					readOnly = true,
					modifier = Modifier
				)
				IconButton(
					onClick = { difficultyOptionsExpanded = true },
					modifier = Modifier.size(40.dp)
				) {
					Icon(
						imageVector = Icons.Default.ArrowDropDown,
						contentDescription = stringResource(R.string.create_recipe_difficulty_label),
						modifier = Modifier.size(40.dp)
					)
				}
				// Difficulty picker
				DropdownMenu (
					modifier = Modifier.fillMaxWidth(0.8f),
					expanded = difficultyOptionsExpanded,
					onDismissRequest = { difficultyOptionsExpanded = false }
				) {
					DropdownMenuItem(
						text = { Text("Facile") },
						onClick = { difficulty = "Facile"; difficultyOptionsExpanded = false }
					)
					DropdownMenuItem(
						text = { Text("Moyen") },
						onClick = { difficulty = "Moyen"; difficultyOptionsExpanded = false }
					)
					DropdownMenuItem(
						text = { Text("Difficile") },
						onClick = { difficulty = "Difficile"; difficultyOptionsExpanded = false }
					)
				}

			}


			Spacer(modifier = Modifier.height(10.dp))

			if (imageUri != null) {
				Image(
					painter = rememberAsyncImagePainter(model = imageUri),
					contentDescription = "Selected Recipe Image",
					modifier = Modifier
						.size(200.dp)
						.padding(bottom = 8.dp)
						.align(Alignment.CenterHorizontally)
				)
			} else {
				Text(
					stringResource(R.string.create_recipe_image_picking_message),
					modifier = Modifier
						.padding(bottom = 8.dp)
						.align(Alignment.CenterHorizontally)
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
				Text(stringResource(R.string.create_recipe_image_picking_button))
			}

			Spacer(modifier = Modifier.height(20.dp))

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
					text = if (private)
						stringResource(R.string.create_post_private_label_1)
					else
						stringResource(R.string.create_post_private_label_2),
					style = MaterialTheme.typography.bodyMedium,
					modifier = Modifier.align(Alignment.CenterVertically)
				)
			}

			Button(
				onClick = {
					if (name.isNotBlank() && ingredientsText.isNotBlank() && stepsText.isNotBlank()) {
						val recipe = Recipe(
							name = name,
							ingredients = ingredientsText.split("\n"),
							steps = stepsText.split("\n"),
							imageUrl = imageUri?.toString() ?: "",
							communityId = selectedCommunityId,
							isPrivate = private,
							prepTime = "$time min",
							difficulty = difficulty
						)
						createRecipe(recipe)
						goBack()
					} else {
						Log.d("RecipePost", "Name, ingredients, and steps cannot be blank.")
					}
				},
				modifier = Modifier.align(Alignment.CenterHorizontally).width(150.dp)
			) {
				Text(
					text = stringResource(R.string.create_post_button),
					style = MaterialTheme.typography.labelSmall,
					modifier = Modifier.padding(vertical = 8.dp)
				)
			}
		}
	}

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