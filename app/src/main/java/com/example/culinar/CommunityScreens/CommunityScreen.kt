package com.example.culinar.CommunityScreens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culinar.ui.theme.darkGrey
import com.example.culinar.ui.theme.grey
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.culinar.R
import com.example.culinar.models.Community
import com.example.culinar.models.Screen
import com.example.culinar.models.viewModels.CommunityViewModel
import com.example.culinar.ui.theme.darkGreen
import com.example.culinar.ui.theme.lightGreen
import com.example.culinar.ui.theme.mediumGreen
import com.example.culinar.viewmodels.SessionViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import com.example.culinar.models.viewModels.GENERAL_POSTS_FIREBASE_COLLECTION
import com.example.culinar.models.viewModels.GeneralPostViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



@Composable
		/**
		 * Main screen for the community section.
		 *
		 * Displays the community space title, description, and buttons to:
		 * - Access "My Community" if the user is an expert and part of a community.
		 * - Create a new community if the user is an expert without a community.
		 * - Join a community for all logged-in users.
		 * Handles navigation based on user actions and session state.
		 *
		 * @param modifier Modifier for styling the composable.
		 * @param communityViewModel ViewModel managing community data and actions.
		 * @param sessionViewModel ViewModel managing user session and authentication.
		 * @param onNavigate Lambda for navigation between screens, takes destination route and optional params.
		 */
fun CommunityScreen (
	modifier: Modifier = Modifier,
	communityViewModel: CommunityViewModel = CommunityViewModel(),
	sessionViewModel: SessionViewModel = viewModel(),
	onNavigate: (String, String?) -> Unit = { _, _ -> },
) {

	val userId by sessionViewModel.id.collectAsState()
	val isExpert by sessionViewModel.isExpert.collectAsState()

	// Log current user session info for debugging
	Log.d("CommunityScreen", "userId: $userId, ${if(isExpert == true) "is expert" else "is not expert"}")

	// Set current user ID into the community view model for data filtering
	communityViewModel.setUserId(userId ?: "")
	val myCommunity = communityViewModel.myCommunity.collectAsState().value

	// Layout container for all screen content
	Column (
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier.fillMaxSize()
	) {

		// Header row with screen title
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center,
			modifier = Modifier.fillMaxWidth()
				.height(80.dp)
				.background(color = grey)
		) {
			// Title text styled for emphasis
			Text(
				text = stringResource(R.string.community_space_title),
				fontSize = 25.sp,
				fontFamily = FontFamily.Serif,
				textAlign = TextAlign.Center,
				fontWeight = FontWeight.Bold,
				lineHeight = 50.sp,
				color = Color(0xFF3CB460),
				modifier = modifier
					.height(50.dp)
					.background(color = grey)
			)
		}

		// Spacer to push description lower on screen, flexibly sized
		Spacer(modifier = Modifier.weight(1f))

		// Description text explaining the community space purpose
		Text(
			stringResource(R.string.community_space_description),
			style = MaterialTheme.typography.bodyLarge,
			lineHeight = 50.sp,
			textAlign = TextAlign.Center,
			modifier = modifier.padding(16.dp)
		)

		// Fixed vertical space between description and buttons
		Spacer(modifier = Modifier.height(40.dp))

		// Conditional buttons based on user expertise and community membership
		if (isExpert == true) {
			if (myCommunity != null) {
				// Button to access user's own community
				TextButton(
					onClick = {
						communityViewModel.selectCommunity(communityViewModel.myCommunity.value!!)
						onNavigate(Screen.MyCommunity.name, null)
					},
					shape = CutCornerShape(5.dp),
					modifier = modifier.height(50.dp),
					colors = ButtonColors(
						containerColor = lightGreen,
						contentColor = Color.White,
						disabledContainerColor = mediumGreen,
						disabledContentColor = Color.White
					)
				) {
					Text(
						text = stringResource(R.string.my_community_button),
						fontSize = 20.sp,
						fontFamily = FontFamily.Serif,
						textAlign = TextAlign.Center,
					)
				}
			} else {
				// Button to create a new community if none exists
				TextButton(
					onClick = { onNavigate(Screen.CreateCommunity.name, null) },
					shape = CutCornerShape(5.dp),
					modifier = modifier.height(50.dp),
					colors = ButtonColors(
						containerColor = lightGreen,
						contentColor = Color.White,
						disabledContainerColor = mediumGreen,
						disabledContentColor = Color.White
					)
				) {
					Text(
						text = stringResource(R.string.create_community_button),
						fontSize = 20.sp,
						fontFamily = FontFamily.Serif,
						textAlign = TextAlign.Center,
					)
				}
			}
		}

		// Spacer between expert options and join button
		Spacer(modifier = Modifier.height(20.dp))

		// Button to join a community or prompt login if not logged in
		TextButton(
			onClick = {
				if (userId != null)
					onNavigate(Screen.ListCommunities.name, null)
				else
					onNavigate("${Screen.Account.name}?nextRoute=${Screen.Community.name}", null)
			},
			shape = CutCornerShape(5.dp),
			modifier = modifier.height(80.dp),
			colors = ButtonColors(
				containerColor = mediumGreen,
				contentColor = Color.White,
				disabledContainerColor = darkGreen,
				disabledContentColor = Color.White
			)
		) {
			Text(
				text = if (userId != null)
					stringResource(R.string.join_community_button_default)
				else
					stringResource(R.string.login_first_button),
				fontSize = 20.sp,
				fontFamily = FontFamily.Serif,
				textAlign = TextAlign.Center,
			)
		}

		// Flexible spacer to push content upwards if needed
		Spacer(modifier = Modifier.weight(1f))
	}

}


@Composable
		/**
		 * Screen for creating a new community.
		 *
		 * Manages a multi-step process using an internal screen number state:
		 * - Step 0: Initial start screen to begin creation.
		 * - Step 1: Details input screen to finalize community creation.
		 *
		 * Includes a header with a back button and screen title.
		 *
		 * @param modifier Modifier to style the container.
		 * @param onNavigate Lambda for navigation actions, accepting destination and optional params.
		 * @param communityViewModel ViewModel managing community-related data and actions.
		 */
fun CreateCommunity(
	modifier: Modifier = Modifier,
	onNavigate: (String, String?) -> Unit,
	communityViewModel: CommunityViewModel = viewModel()
) {

	// Current step/screen in the community creation flow
	var screenNumber by remember { mutableIntStateOf(0) }

	// Function to update the current screen number (step)
	val changeOnboardingScreen: (Int) -> Unit = { screenNumber = it }

	// Main vertical container for the entire screen content
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier.fillMaxSize()
	) {

		// Header row with back button and screen title
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth()
				.height(80.dp)
				.background(color = grey)
		) {
			// Back/cancel button to return to the community screen
			TextButton(
				onClick = { onNavigate(Screen.Community.name, null) },
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
					tint = darkGrey,
					modifier = modifier.height(100.dp).width(45.dp)
				)
			}

			// Title text for the create community screen
			Text(
				text = "Démarrer une communauté",
				fontSize = 20.sp,
				fontFamily = FontFamily.Serif,
				textAlign = TextAlign.Center,
				fontWeight = FontWeight.Bold,
				lineHeight = 50.sp,
				color = Color(0xFF3CB460),
				modifier = modifier
					.height(50.dp)
					.background(color = grey)
			)
		}

		// Switch displayed content depending on current screen number (step)
		when (screenNumber) {
			0 -> CreateCommunityStart(changeOnboardingScreen = changeOnboardingScreen)
			1 -> CreateCommunityDetails(communityViewModel = communityViewModel, onNavigate = onNavigate)
		}
	}

}


@Composable
		/**
		 * First step screen for creating a community.
		 *
		 * Displays the terms and conditions text and a checkbox for the user to accept them.
		 * Only allows continuing to the next step if the terms are accepted.
		 *
		 * @param modifier Modifier to style the container.
		 * @param changeOnboardingScreen Lambda to update the current step in the creation flow.
		 */
fun CreateCommunityStart(
	modifier: Modifier = Modifier,
	changeOnboardingScreen: (Int) -> Unit = {}
) {

	// Tracks whether the user has accepted the terms and conditions
	var acceptedTerms by remember { mutableStateOf(false) }

	// Main vertical container centered horizontally and vertically
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = modifier.fillMaxSize()
	) {
		// Read-only text field displaying the terms and conditions text
		TextField(
			value = stringResource(R.string.expert_terms_and_conditions),
			readOnly = true,
			textStyle = TextStyle(textAlign = TextAlign.Justify),
			onValueChange = {},
			label = {
				Text(
					"Termes et conditions d'utilisation",
					color = Color.Black,
					fontWeight = FontWeight.Bold
				)
			},
			modifier = modifier.padding(16.dp).height(300.dp)
		)

		Spacer(modifier = Modifier.height(16.dp))

		// Checkbox with label to accept terms and conditions
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically
		) {
			Checkbox(
				checked = acceptedTerms,
				onCheckedChange = { acceptedTerms = it }
			)
			TextButton(
				onClick = { acceptedTerms = !acceptedTerms }
			) {
				Text("J'accepte les termes et conditions d'utilisation")
			}
		}

		// Button to continue to next screen, enabled only if terms accepted
		Button(
			onClick = { changeOnboardingScreen(1) },
			enabled = acceptedTerms,
			colors = ButtonDefaults.buttonColors(
				containerColor = mediumGreen,
				contentColor = Color.White,
				disabledContainerColor = grey,
				disabledContentColor = Color.Black
			)
		) {
			Text("Continuer")
		}
	}
}


@Composable
		/**
		 * Screen to enter details for creating a new community.
		 *
		 * Allows the user to input the community name and description,
		 * accept terms and conditions, and submit to create the community.
		 *
		 * @param modifier Modifier to style the container.
		 * @param communityViewModel ViewModel handling community data and operations.
		 * @param onNavigate Lambda to handle navigation events.
		 */
fun CreateCommunityDetails(
	modifier: Modifier = Modifier,
	communityViewModel: CommunityViewModel = CommunityViewModel(),
	onNavigate: (String, String?) -> Unit
) {

	// Tracks if the community can be created (true if name is not empty)
	var creatable by remember { mutableStateOf(true) }
	// Tracks if the user has accepted terms and conditions
	var acceptedTerms by remember { mutableStateOf(false) }
	// Community name state
	var name by remember { mutableStateOf("Communauté X") }
	// Community description state
	var description by remember { mutableStateOf("Que faisons-nous ?") }

	// Main vertical container centered horizontally and vertically
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = modifier.fillMaxSize()
	) {
		// Input field for community name
		TextField(
			value = name,
			textStyle = TextStyle(textAlign = TextAlign.Justify),
			onValueChange = {
				name = it
				creatable = name != "" // Enable creation only if name is non-empty
			},
			label = {
				Text(
					"Nom de la communauté",
					fontWeight = FontWeight.Bold
				)
			},
			modifier = modifier.padding(16.dp).height(60.dp)
		)

		Spacer(modifier = Modifier.height(16.dp))

		// Input field for community description
		TextField(
			value = description,
			textStyle = TextStyle(textAlign = TextAlign.Justify),
			onValueChange = { description = it },
			label = {
				Text(
					"Description de la communauté",
					fontWeight = FontWeight.Bold
				)
			},
			modifier = modifier.padding(16.dp).height(200.dp)
		)

		Spacer(modifier = Modifier.height(16.dp))

		// Checkbox and label to accept terms and conditions
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically
		) {
			Checkbox(
				checked = acceptedTerms,
				onCheckedChange = { acceptedTerms = it }
			)
			TextButton(
				onClick = { acceptedTerms = !acceptedTerms }
			) {
				Text(
					"J'ai lu et j'accepte les termes et conditions d'utilisation",
					modifier = modifier.width(230.dp)
				)
			}
		}

		// Button to create community, enabled only if terms accepted and name non-empty
		Button(
			onClick = {
				communityViewModel.addCommunity(Community(name, description))
				onNavigate(Screen.Community.name, null)
			},
			enabled = acceptedTerms && creatable,
			colors = ButtonDefaults.buttonColors(
				containerColor = mediumGreen,
				contentColor = Color.White,
				disabledContainerColor = grey,
				disabledContentColor = Color.Black
			)
		) {
			Text("Créer la communauté")
		}
	}
}


@Composable
		/**
		 * Displays the user's selected community screen with toolbar and post feed.
		 *
		 * Shows a toolbar with navigation and create post options,
		 * and displays the post feed if a community is selected,
		 * otherwise displays a message indicating no community selected.
		 *
		 * @param modifier Modifier to style the container.
		 * @param communityViewModel ViewModel managing community data.
		 * @param goBack Lambda invoked to navigate back.
		 * @param createPost Lambda invoked to create a new post.
		 */
fun MyCommunity(
	modifier: Modifier = Modifier,
	communityViewModel: CommunityViewModel = viewModel(),
	goBack: () -> Unit,
	createPost: () -> Unit,
) {

	// Currently selected community
	val selectedCommunity = communityViewModel.myCommunity.collectAsState()
	Log.d("MyCommunityComposable", "Recomposing. Selected Community: $selectedCommunity")


	// Main container displaying toolbar and post feed or message
	Column {
		// Toolbar with back and create post buttons
		ToolBar(goBack = { goBack() }, community = selectedCommunity.value, createPost = createPost)

		Spacer(modifier.height(10.dp))

		// Conditionally show post feed or message if no community selected
		if (selectedCommunity.value != null) {
			PostFeed(communityViewModel = communityViewModel)
		} else {
			Text(text = "No community selected")
		}
	}

}




@Composable
		/**
		 * Screen displaying a list of communities for browsing.
		 *
		 * Allows selecting a community which triggers navigation to the community feed,
		 * and provides a way to navigate back to the main community screen.
		 *
		 * @param modifier Modifier for styling the container.
		 * @param onNavigate Lambda to handle navigation actions.
		 * @param communityViewModel ViewModel managing community data.
		 */
fun ListCommunities(
	modifier: Modifier = Modifier,
	onNavigate: (String, String?) -> Unit,
	communityViewModel: CommunityViewModel = viewModel()
) {
	// Displays the list of communities for browsing
	BrowseCommunities(
		toCommunity = { community ->
			// Select community and navigate to feed screen
			communityViewModel.selectCommunity(community)
			onNavigate(Screen.Feed.name, null)
		},
		communityViewModel = communityViewModel,
		// Navigate back to main community screen
		backToHome = { onNavigate(Screen.Community.name, null) }
	)
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PreviewCommunityScreen() {
	CommunityScreen()
}



fun rotateImageIfRequired(context: Context, imageUri: Uri): Bitmap? {
	val inputStream = context.contentResolver.openInputStream(imageUri)
	val bitmap = BitmapFactory.decodeStream(inputStream)
	inputStream?.close()

	val fileDescriptor = context.contentResolver.openFileDescriptor(imageUri, "r")?.fileDescriptor
	val exif = fileDescriptor?.let { ExifInterface(it) }

	val orientation = exif?.getAttributeInt(
		ExifInterface.TAG_ORIENTATION,
		ExifInterface.ORIENTATION_NORMAL
	) ?: ExifInterface.ORIENTATION_NORMAL

	val rotationAngle = when (orientation) {
		ExifInterface.ORIENTATION_ROTATE_90 -> 90f
		ExifInterface.ORIENTATION_ROTATE_180 -> 180f
		ExifInterface.ORIENTATION_ROTATE_270 -> 270f
		else -> 0f
	}

	return bitmap?.let {
		if (rotationAngle != 0f) {
			val matrix = Matrix()
			matrix.postRotate(rotationAngle)
			Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
		} else {
			it
		}
	}
}

@Composable
fun PostFeed(navController: NavController) {
	CameraCaptureScreen(navController = navController)
}


@Composable
fun CameraCaptureScreen(navController: NavController) {
	val context = LocalContext.current
	val lifecycleOwner = LocalLifecycleOwner.current

	val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

	var imageUri by remember { mutableStateOf<Uri?>(null) }
	val imageCapture = remember { ImageCapture.Builder().build() }

	val galleryLauncher = rememberLauncherForActivityResult (
		contract = ActivityResultContracts.GetContent()
	) { uri: Uri? ->
		uri?.let {
			navController.navigate("photoPreview?uri=${Uri.encode(it.toString())}")
		}
	}

	val outputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

	Column(modifier = Modifier.fillMaxSize()) {
		AndroidView(
			modifier = Modifier
				.weight(1f)
				.fillMaxWidth(),
			factory = { ctx ->
				val previewView = PreviewView(ctx)

				cameraProviderFuture.addListener({
					val cameraProvider = cameraProviderFuture.get()
					val preview = Preview.Builder().build().also {
						it.surfaceProvider = previewView.surfaceProvider
					}

					val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

					try {
						cameraProvider.unbindAll()
						cameraProvider.bindToLifecycle(
							lifecycleOwner,
							cameraSelector,
							preview,
							imageCapture
						)
					} catch (e: Exception) {
						Log.e("CameraX", "Binding failed", e)
					}

				}, ContextCompat.getMainExecutor(ctx))

				previewView
			}
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalArrangement = Arrangement.SpaceEvenly
		) {
			Button(
				onClick = {
					val photoFile = File(
						outputDirectory,
						SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".jpg"
					)
					val photoUri = FileProvider.getUriForFile(
						context,
						"${context.packageName}.fileprovider",
						photoFile
					)
					val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
					imageCapture.takePicture(
						outputOptions,
						ContextCompat.getMainExecutor(context),
						object : ImageCapture.OnImageSavedCallback {
							override fun onError(exc: ImageCaptureException) {
								Log.e("CameraX", "Photo capture failed: ${exc.message}", exc)
							}

							override fun onImageSaved(output: ImageCapture.OutputFileResults) {
								photoUri.let {
									navController.navigate("photoPreview?uri=${Uri.encode(it.toString())}")
								}
							}
						}
					)
				},
				colors = ButtonDefaults.buttonColors(
					containerColor = grey,
					contentColor = Color.Black
				)
			) {
				Text("Prendre une photo")
			}

			Button(
				onClick = {
					galleryLauncher.launch("image/*")
				},
				colors = ButtonDefaults.buttonColors(
					containerColor = grey,
					contentColor = Color.Black
				)
			) {
				Text("Depuis galerie")
			}
		}
	}
}


@Composable
fun PhotoPreviewScreen(imageUriString: String?) {
	val context = LocalContext.current
	val imageUri = imageUriString?.toUri()

	val bitmap by remember(imageUri) {
		mutableStateOf(
			imageUri?.let {
				rotateImageIfRequired(context, it)
			}
		)
	}

	Surface(
		modifier = Modifier
			.fillMaxSize()
			.background(Color.Black)
	) {
		bitmap?.let {
			Image(
				bitmap = it.asImageBitmap(),
				contentDescription = "Photo preview",
				contentScale = ContentScale.Fit,
				modifier = Modifier
					.fillMaxSize()
					.padding(16.dp)
					.clip(RoundedCornerShape(16.dp))
			)
		} ?: run {
			Box(
				contentAlignment = Alignment.Center,
				modifier = Modifier.fillMaxSize()
			) {
				Text("Impossible de charger l'image", color = Color.White)
			}
		}
	}
}

data class Comment(
	val id: String = "",
	val userId: String = "",
	val content: String = "",
	val timestamp: Date? = null,
	val postId: String = "",
	val username: String = "Utilisateur inconnu",
	)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckFeed(
	navController: NavController,
	sessionViewModel: SessionViewModel = viewModel(),
	generalPostViewModel: GeneralPostViewModel = viewModel()
) {
	val context = LocalContext.current
	val allPosts by generalPostViewModel.allPosts.collectAsState()
	val idConnect by sessionViewModel.id.collectAsState()
	val currentUserId = idConnect // ✅ pour éviter l'erreur "smart cast"

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Fil d’actualité", style = MaterialTheme.typography.titleLarge) },
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.primary,
					titleContentColor = MaterialTheme.colorScheme.onPrimary
				)
			)
		}
	) { padding ->
		LazyColumn(
			modifier = Modifier
				.padding(padding)
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background),
			verticalArrangement = Arrangement.spacedBy(12.dp),
			contentPadding = PaddingValues(16.dp)
		) {
			items(allPosts.sortedByDescending { it.date }) { post ->
				val isLiked = currentUserId != null && currentUserId in post.likes

				Card(
					modifier = Modifier.fillMaxWidth(),
					shape = RoundedCornerShape(16.dp),
					colors = CardDefaults.cardColors(
						containerColor = MaterialTheme.colorScheme.surface
					),
					elevation = CardDefaults.cardElevation(4.dp)
				) {
					Column(modifier = Modifier.padding(16.dp)) {
						Text(
							text = post.username,
							style = MaterialTheme.typography.bodyLarge.copy(
								color = MaterialTheme.colorScheme.primary
							)
						)
						Spacer(modifier = Modifier.height(6.dp))
						Text(
							text = post.name,
							style = MaterialTheme.typography.titleMedium.copy(
								color = MaterialTheme.colorScheme.onSurface
							)
						)
						post.date?.let {
							val dateFormatted = SimpleDateFormat(
								"dd MMM yyyy à HH:mm", Locale.getDefault()
							).format(it)
							Spacer(modifier = Modifier.height(4.dp))
							Text(
								text = dateFormatted,
								style = MaterialTheme.typography.bodySmall.copy(
									color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
								)
							)
						}
						Spacer(modifier = Modifier.height(8.dp))
						Text(
							text = post.content,
							style = MaterialTheme.typography.bodySmall.copy(
								color = MaterialTheme.colorScheme.onSurface
							)
						)
						Spacer(modifier = Modifier.height(12.dp))
						Row(
							horizontalArrangement = Arrangement.spacedBy(16.dp),
							modifier = Modifier.fillMaxWidth()
						) {
							IconButton(
								onClick = {
									if (currentUserId == null) {
										Toast.makeText(context, "Connectez-vous pour liker", Toast.LENGTH_SHORT).show()
										return@IconButton
									}

									if (isLiked) {
										generalPostViewModel.unlikePost(post, currentUserId)
									} else {
										generalPostViewModel.likePost(post, currentUserId)
									}
								}
							) {
								Icon(
									imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
									contentDescription = "Like",
									tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
								)
							}

							IconButton(onClick = {
								navController.navigate("comments/${post.id}")
							}) {
								Icon(
									imageVector = Icons.Default.Comment,
									contentDescription = "Commentaire",
									tint = MaterialTheme.colorScheme.secondary
								)
							}
						}
					}
				}
			}
		}
	}
}


@Composable
fun CommentItem(comment: Comment) {
	Column {
		Text(
			text = comment.username,
			style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
			color = MaterialTheme.colorScheme.primary
		)
		Spacer(modifier = Modifier.height(2.dp))
		Text(
			text = comment.content,
			style = MaterialTheme.typography.bodyMedium
		)
		comment.timestamp?.let {
			val formattedDate = SimpleDateFormat(
				"dd MMM yyyy à HH:mm", Locale.getDefault()
			).format(it)
			Text(
				text = formattedDate,
				style = MaterialTheme.typography.bodySmall.copy(
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
				)
			)
		}
		Spacer(modifier = Modifier.height(4.dp))
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(postId: String, navController: NavController) {
	val context = LocalContext.current
	var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
	var isLoading by remember { mutableStateOf(true) }

	LaunchedEffect(postId) {
		val db = Firebase.firestore
		db.collection(GENERAL_POSTS_FIREBASE_COLLECTION).document(postId).collection("Comments")
			.get()
			.addOnSuccessListener { snapshot ->
				comments = snapshot.documents.map { doc ->
					doc.toObject(Comment::class.java)!!.copy(id = doc.id)
					}.sortedByDescending { it.timestamp }

				Log.d("CommentsScreen", "Loaded ${comments.size} comments")
				isLoading = false
			}
			.addOnFailureListener {
				Toast.makeText(context, "Erreur lors du chargement des commentaires", Toast.LENGTH_SHORT).show()
				isLoading = false
			}
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Commentaires") },
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
						Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
					}
				}
			)
		}
	) { padding ->
		if (isLoading) {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				CircularProgressIndicator()
			}
		} else {
			LazyColumn(
				modifier = Modifier.padding(padding)
			) {
				items(comments) { comment ->
					CommentItem(comment)
					Divider()
				}
			}
		}
	}
}







fun createImageFile(context: Context): File {
	val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
	val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
	return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}