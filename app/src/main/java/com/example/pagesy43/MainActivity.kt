package com.example.pagesy43

import com.example.culinar.ui.theme.CulinarTheme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.* // si tu utilises Material3
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.ui.draw.clip
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.io.File
import androidx.core.content.FileProvider
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.util.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culinar.ui.theme.CulinarTheme
import com.example.culinar.ui.theme.Purple80
import com.example.culinar.ui.theme.PurpleGrey80
import com.example.culinar.ui.theme.grey
import com.example.culinar.ui.theme.mediumGreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CulinarTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    BottomNavigation(
        modifier = Modifier.fillMaxWidth().height(60.dp),
        contentColor = Color.White,
        backgroundColor = mediumGreen // Couleur de fond de la barre
    ) {
        BottomNavigationItem(
            icon = { Icon(
                Icons.Default.DateRange,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier.width(40.dp).height(40.dp)
            ) },
            // label = { Text("Calendar") },
            selected = false,
            onClick = { navController.navigate("") }
        )
        BottomNavigationItem(
            icon = { Icon(
                Icons.Default.ShoppingCart,
                contentDescription = "Clear",
                tint = Color.White,
                modifier = Modifier.width(45.dp).height(45.dp)
            ) },
            // label = { Text("Groceries") },
            selected = false,
            onClick = { navController.navigate("") }
        )
        BottomNavigationItem(
            icon = { Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Clear",
                tint = Color.White,
                modifier = Modifier.width(45.dp).height(45.dp)
            ) },
            // label = { Text("Recipies") },
            selected = false,
            onClick = { navController.navigate("") }
        )
        BottomNavigationItem(
            icon = { Icon(
                Icons.Default.Home,
                contentDescription = "Clear",
                tint = Color.White,
                modifier = Modifier.width(45.dp).height(45.dp)
            ) },
            // label = { Text("Home") },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        BottomNavigationItem(
            icon = { Icon(
                Icons.Default.Email,
                contentDescription = "Clear",
                tint = Color.White,
                modifier = Modifier.width(45.dp).height(45.dp)
            ) },
            // label = { Text("Community") },
            selected = false,
            onClick = { navController.navigate("") }
        )
        // Ajoute ici d'autres boutons pour d'autres pages si nécessaire
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val friendViewModel: FriendViewModel = viewModel()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(paddingValues)) {
            composable("home") { Home(navController) }
            composable("PostFeed") { PostFeed(navController) }
            composable("CheckFeed") { CheckFeed() }
            composable("SendMessage") { SendMessage(navController, friendViewModel) }
            composable("AddFriends") { AddFriends(friendViewModel) }

            composable(
                route = "conversation/{username}",
                arguments = listOf(navArgument("username") {
                    type = NavType.StringType
                    nullable = false
                })
            ) { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username")
                username?.let { ConversationScreen(username = it) }
            }

            composable(
                "photoPreview?uri={uri}",
                arguments = listOf(
                    navArgument("uri") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val uri = backStackEntry.arguments?.getString("uri")
                PhotoPreviewScreen(imageUriString = uri)
            }
        }
    }
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
fun Home(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Couleur de fond personnalisée
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.7f) // 70% de la largeur
        ) {
            Button(
                onClick = { navController.navigate("PostFeed") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = grey,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Poster sur mon feed")
            }

            Button(
                onClick = { navController.navigate("CheckFeed") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = grey,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Accéder à mon feed")
            }

            Button(
                onClick = { navController.navigate("SendMessage") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = grey,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Chatter avec des amis")
            }

            Button(
                onClick = { navController.navigate("AddFriends") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = grey,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Ajouter des amis")
            }
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

    val galleryLauncher = rememberLauncherForActivityResult(
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
                        it.setSurfaceProvider(previewView.surfaceProvider)
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
    val imageUri = imageUriString?.let { Uri.parse(it) }

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

@Composable
fun SendMessage(navController: NavController, viewModel: FriendViewModel = viewModel()) {
    var searchText by remember { mutableStateOf("") }

    val filteredList = viewModel.friends.filter {
        it.contains(searchText, ignoreCase = true)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Rechercher...") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { /* action pour nouvelle conversation */ },
                colors = ButtonDefaults.buttonColors(containerColor = grey)
            ) {
                Text("+", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            filteredList.forEach { name ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("conversation/${Uri.encode(name)}")
                        },
                    colors = CardDefaults.cardColors(containerColor = grey)
                ) {
                    Text(
                        text = name,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}



@Composable
fun ConversationScreen(username: String) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Titre
        Text(
            text = "Discussion avec ${username ?: "Inconnu"}",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Liste des messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = grey),
                        modifier = Modifier.widthIn(max = 250.dp)
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // Champ de saisie + bouton
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Écris un message...") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        messages.add(messageText)
                        messageText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = grey,
                    contentColor = Color.Black
                )
            ) {
                Text("Envoyer")
            }
        }
    }
}

class FriendViewModel : ViewModel() {
    val friends = mutableStateListOf<String>()

    fun addFriend(name: String) {
        if (!friends.contains(name)) {
            friends.add(name)
        }
    }
}

@Composable
fun AddFriends(viewModel: FriendViewModel = viewModel()) {
    val allUsers = listOf("Alice", "Bob", "Charlie", "David", "Emma", "Fatima", "George", "Hassan", "Léa")
    var searchText by remember { mutableStateOf("") }

    val filteredUsers = allUsers.filter {
        it.contains(searchText, ignoreCase = true) && it !in viewModel.friends
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Rechercher un ami...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(filteredUsers) { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = user, style = MaterialTheme.typography.bodyLarge)
                    Button(
                        onClick = { viewModel.addFriend(user) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = grey,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Ajouter")
                    }
                }
            }
        }
    }
}


fun createImageFile(context: android.content.Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}

