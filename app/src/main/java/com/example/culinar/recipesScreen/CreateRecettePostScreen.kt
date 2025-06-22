package com.example.culinar.recipesScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.culinar.models.viewModels.RecettePostViewModel

@Composable
fun CreateRecettePostScreen(
    viewModel: RecettePostViewModel = viewModel(),
    onPostCreated: () -> Unit,
    onCancel: () -> Unit
) {
    var content by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri.toString()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Publier une nouvelle réalisation", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Bouton pour choisir une image
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Choisir une image")
        }

        imageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = "Image sélectionnée",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Row {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Annuler")
            }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = {
                    isLoading = true
                    viewModel.createPost(content, imageUri ?: "") {
                        isLoading = false
                        onPostCreated()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = content.isNotBlank() && imageUri != null && !isLoading
            ) {
                Text(if (isLoading) "En cours..." else "Publier")
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun  CreateRecettePostScreenPreview(){
    CreateRecettePostScreen(onPostCreated = {}, onCancel = {})

}