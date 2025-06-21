package com.example.culinar.recipesScreen


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun PublishPostScreen() {
    val context = LocalContext.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nouvelle publication", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Choisir une image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "un post de recette",
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Description :")
        BasicTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(8.dp),
            textStyle = TextStyle(fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (imageUri != null && description.isNotBlank()) {
                    isUploading = true
                    uploadPost(imageUri!!, description, context) {
                        isUploading = false
                        description = ""
                        imageUri = null
                    }
                } else {
                    Toast.makeText(context, "Veuillez ajouter une image et une description", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isUploading
        ) {
            Text(if (isUploading) "Publication en cours..." else "Publier")
        }
    }
}

// envoie des données

fun uploadPost(
    imageUri: Uri,
    description: String,
    context: android.content.Context,
    onComplete: () -> Unit
) {
    val storageRef = FirebaseStorage.getInstance().reference
    val imageRef = storageRef.child("posts/${UUID.randomUUID()}.jpg")

    val uploadTask = imageRef.putFile(imageUri)

    uploadTask.addOnSuccessListener {
        imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
            val username = FirebaseAuth.getInstance().currentUser?.displayName
                ?: FirebaseAuth.getInstance().currentUser?.email?.substringBefore("@")

            val postMap = hashMapOf(
                "imageUrl" to downloadUrl.toString(),
                "description" to description,
                "timestamp" to System.currentTimeMillis(),
                "username" to username
            )

            FirebaseFirestore.getInstance()
                .collection("Publications")
                .add(postMap)
                .addOnSuccessListener {
                    Toast.makeText(context, "Publication réussie !", Toast.LENGTH_SHORT).show()
                    onComplete()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erreur lors de l’envoi", Toast.LENGTH_SHORT).show()
                    onComplete()
                }
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Échec de l’upload de l’image", Toast.LENGTH_SHORT).show()
        onComplete()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PublishPostScreenPreview() {
    PublishPostScreen()
}

