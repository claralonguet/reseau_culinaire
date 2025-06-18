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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun CommunityScreen (
	modifier: Modifier = Modifier,
	communityViewModel: CommunityViewModel = CommunityViewModel(),
	navController: NavController = NavController(LocalContext.current),
	userId: String = "",
	username: String = "",
	isExpert: Boolean = false
) {

	/*TODO: Replace declaration using the actual user status*/
	val isExpert = true
	// communityViewModel.refreshCommunities()

	// Screen content
	Column (
		//verticalArrangement = Arrangement.SpaceEvenly,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier.fillMaxSize()
	) {

		// Screen title and options
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center,
			modifier = Modifier.fillMaxWidth()
				.height(80.dp)
				.background(color = grey)
		) {
			// Title of the subscreen
			Text(
				text = "Espace communauté",
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

		Spacer(modifier = Modifier.weight(1f))

		// Screen description
		Text(stringResource(R.string.community_space_description),
			fontSize = 20.sp,
			fontFamily = FontFamily.Serif,
			textAlign = TextAlign.Center,
			fontWeight = FontWeight.SemiBold,
			lineHeight = 50.sp,
			modifier = modifier.padding(16.dp)
		)

		Spacer(modifier = Modifier.height(40.dp))

		// Options
		if(isExpert) {
			if(communityViewModel.myCommunity.value != null) {
				// ... My community
				TextButton(
					onClick = { navController.navigate(Screen.MyCommunity.name) },
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
						text = "Ma communauté",
						fontSize = 20.sp,
						fontFamily = FontFamily.Serif,
						textAlign = TextAlign.Center,
					)
				}
			} else {
				// ... Create a community
				TextButton(
					onClick = { navController.navigate(Screen.CreateCommunity.name) },
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
						text = "Créer une communauté",
						fontSize = 20.sp,
						fontFamily = FontFamily.Serif,
						textAlign = TextAlign.Center,
					)
				}
			}
		}

		Spacer(modifier = Modifier.height(20.dp))

		// ... Join a community
		TextButton(
			onClick = { navController.navigate(Screen.ListCommunities.name) },
			shape = CutCornerShape(5.dp),
			modifier = modifier.height(50.dp),
			colors = ButtonColors(
				containerColor = mediumGreen,
				contentColor = Color.White,
				disabledContainerColor = darkGreen,
				disabledContentColor = Color.White
			)
		) {
			Text(
				text = "Rejoindre une communauté",
				fontSize = 20.sp,
				fontFamily = FontFamily.Serif,
				textAlign = TextAlign.Center,
			)
		}

		Spacer(modifier = Modifier.weight(1f))

	}

}


@Composable
fun CreateCommunity(modifier: Modifier = Modifier, navController: NavController = NavController(LocalContext.current), communityViewModel: CommunityViewModel = viewModel()) {

	var screenNumber by remember { mutableIntStateOf(0) }
	val changeOnboardingScreen: (Int) -> Unit = { screenNumber = it }

	// Screen content
	Column (
		//verticalArrangement = Arrangement.SpaceEvenly,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier.fillMaxSize()
	) {

		// Screen title and options
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth()
				.height(80.dp)
				.background(color = grey)
		) {
			// Return button
			TextButton(
				onClick = { navController.navigate(Screen.Community.name) },
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


			// Title of the subscreen
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

		when (screenNumber) {
			0 -> CreateCommunityStart(changeOnboardingScreen = changeOnboardingScreen)
			1 -> CreateCommunityDetails(communityViewModel = communityViewModel, navController = navController)
		}
	}

}


@Composable
fun CreateCommunityStart(modifier: Modifier = Modifier, changeOnboardingScreen: (Int) -> Unit = {}) {

	var acceptedTerms by remember { mutableStateOf(false) }

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = modifier.fillMaxSize()
	) {
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

		// Accept terms and conditions
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically
		) {
			Checkbox(
				checked = acceptedTerms,
				onCheckedChange = { acceptedTerms = it },

				)
			TextButton(
				onClick = { acceptedTerms = !acceptedTerms },
			) {
				Text("J'accepte les termes et conditions d'utilisation")
			}
		}


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
fun CreateCommunityDetails(modifier: Modifier = Modifier, communityViewModel: CommunityViewModel = CommunityViewModel(), navController: NavController) {

	var creatable by remember { mutableStateOf(true) }
	var acceptedTerms by remember { mutableStateOf(false) }
	var name by remember { mutableStateOf("Communauté X") }
	var description by remember { mutableStateOf("Que faisons-nous ?") }

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
		modifier = modifier.fillMaxSize()
	) {
		// Community name
		TextField(
			value = name,
			textStyle = TextStyle(textAlign = TextAlign.Justify),
			onValueChange = {
				name = it
				creatable = name != ""
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

		// Community description
		TextField(
			value = description,
			textStyle = TextStyle(textAlign = TextAlign.Justify),
			onValueChange = { description = it },
			label = {
				Text(
					"Nom de la communauté",
					fontWeight = FontWeight.Bold
				)
			},
			modifier = modifier.padding(16.dp).height(60.dp)
		)
		Spacer(modifier = Modifier.height(16.dp))

		// Accept terms and conditions
		Row(
			horizontalArrangement = Arrangement.Center,
			verticalAlignment = Alignment.CenterVertically
		) {
			Checkbox(
				checked = acceptedTerms,
				onCheckedChange = { acceptedTerms = it },

				)
			TextButton(
				onClick = { acceptedTerms = !acceptedTerms },
			) {
				Text("J'ai lu et j'accepte les termes et conditions d'utilisation", modifier = modifier.width(230.dp))
			}
		}

		// Create community button
		Button(
			onClick = {
				communityViewModel.addCommunity(Community(name, description))
				navController.navigate(Screen.Community.name)
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
fun MyCommunity(modifier: Modifier = Modifier, communityViewModel: CommunityViewModel = viewModel(), navController: NavController = NavController(LocalContext.current)) {

}



@Composable
fun ListCommunities(modifier: Modifier = Modifier, navController: NavController = NavController(LocalContext.current), communityViewModel: CommunityViewModel = viewModel()) {
	BrowseCommunities(
		toCommunity = { community ->
			communityViewModel.selectCommunity(community)
			navController.navigate(Screen.Feed.name)
		},
		backToHome = { navController.navigate(Screen.Community.name) }
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


@Composable
fun CheckFeed() {
	Surface(
		modifier = Modifier.fillMaxSize(),
		color = Color(0xFFD1C4E9)
	) {
		Box(
			contentAlignment = Alignment.Center
		) {
			Text(text = "Accéder à mon feed", style = MaterialTheme.typography.headlineMedium)
		}
	}
}

fun createImageFile(context: Context): File {
	val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
	val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
	return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}