package com.example.culinar.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

data class ExpertRequest(
    val userId: String,
    val timestamp: Timestamp?,
    val attestationUrl: String?
)

@Composable
fun ExpertRequestsScreen(
    db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var expertRequests by remember { mutableStateOf<List<ExpertRequest>>(emptyList()) }
    var userNamesMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Charger toutes les demandes d'expertise
        db.collection("expert").get()
            .addOnSuccessListener { documents ->
                val requests = documents.documents.mapNotNull { doc ->
                    val userId = doc.id
                    val timestamp = doc.getTimestamp("timestamp")
                    val attestationUrl = doc.getString("attestationUrl")
                    ExpertRequest(userId, timestamp, attestationUrl)
                }
                expertRequests = requests

                // Charger usernames pour chaque userId
                val ids = requests.map { it.userId }
                if (ids.isNotEmpty()) {
                    db.collection("Utilisateur")
                        .whereIn(FieldPath.documentId(), ids)
                        .get()
                        .addOnSuccessListener { usersDocs ->
                            val map = usersDocs.documents.associate { it.id to (it.getString("username") ?: "Inconnu") }
                            userNamesMap = map
                            isLoading = false
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Erreur récupération utilisateurs", Toast.LENGTH_SHORT).show()
                            isLoading = false
                        }
                } else {
                    isLoading = false
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Erreur récupération demandes", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (expertRequests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Aucune demande d'expertise en attente")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(expertRequests) { request ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Utilisateur ID : ${request.userId}", style = MaterialTheme.typography.bodyMedium)
                            Text("Username : ${userNamesMap[request.userId] ?: "Chargement..."}", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Date de la demande : ${
                                    request.timestamp?.toDate()?.let { java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(it) }
                                        ?: "Inconnue"
                                }",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            // Valider la demande : mettre expert = true dans Utilisateur et supprimer la demande dans expert
                                            val userRef = db.collection("Utilisateur").document(request.userId)
                                            userRef.update("expert", true)
                                                .addOnSuccessListener {
                                                    db.collection("expert").document(request.userId)
                                                        .delete()
                                                        .addOnSuccessListener {
                                                            Toast.makeText(context, "Demande validée", Toast.LENGTH_SHORT).show()
                                                            // Rafraichir la liste
                                                            expertRequests = expertRequests.filter { it.userId != request.userId }
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Toast.makeText(context, "Erreur suppression demande: ${e.message}", Toast.LENGTH_SHORT).show()
                                                        }
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(context, "Erreur validation: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Valider")
                                }
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            // Rejeter la demande : supprimer la demande dans expert
                                            db.collection("expert").document(request.userId)
                                                .delete()
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "Demande rejetée", Toast.LENGTH_SHORT).show()
                                                    expertRequests = expertRequests.filter { it.userId != request.userId }
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(context, "Erreur rejet: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Rejeter")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
