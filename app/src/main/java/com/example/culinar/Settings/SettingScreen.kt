package com.example.culinar.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.culinar.viewmodels.SessionViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun SettingScreen(
    sessionViewModel: SessionViewModel,
    navController: NavController,
    onRequestSent: () -> Unit
) {
    val context = LocalContext.current
    val db = Firebase.firestore

    val username by sessionViewModel.username.collectAsState()
    val expert by sessionViewModel.isExpert.collectAsState()
    val id by sessionViewModel.id.collectAsState()
    val isAdmin by sessionViewModel.isAdmin.collectAsState()

    var isUploading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Valeur de expert = $expert",
            style = MaterialTheme.typography.bodyLarge,
            color = if (expert == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Conditions Générales d'Utilisation",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            Text(
                text = """
                    Bienvenue sur Culinar !
                    
                    Voici les conditions générales d'utilisation...
                    (Ajoutez ici le contenu complet des CGU)
                """.trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if ( expert != true) {
            Text(
                text = "Demande d'expertise",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (!isUploading && !id.isNullOrBlank()) {
                        isUploading = true
                        val expertRequest = hashMapOf(
                            "userId" to id,
                            "timestamp" to Timestamp.now()
                        )
                        db.collection("expert")
                            .document(id!!)
                            .set(expertRequest)
                            .addOnSuccessListener {
                                isUploading = false
                                Toast.makeText(context, "Demande envoyée avec succès", Toast.LENGTH_LONG).show()
                                onRequestSent()
                            }
                            .addOnFailureListener { e ->
                                isUploading = false
                                Toast.makeText(context, "Erreur lors de l'envoi: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(context, "Utilisateur non valide ou en cours d'envoi", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isUploading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Soumettre ma demande")
                }
            }
        }
        else {
            Text(
                text = "✅ Vous êtes déjà reconnu comme expert.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        if (isAdmin == true) {
            Button(
                onClick = {
                    navController.navigate("PendingExpertRequests")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔍 Expertise en attente")
            }
        }
    }
}
