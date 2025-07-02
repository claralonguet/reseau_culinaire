package com.example.culinar.CommunityScreens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culinar.models.viewModels.FriendViewModel
import com.example.culinar.ui.theme.grey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Utilisateur(val id: String, val username: String)
data class Ami(val id: String, val username: String)

@Composable
fun SendMessage(
    navController: NavController,
    currentUserId: String,
    viewModel: FriendViewModel = viewModel()
) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    val mutualFriends = remember { mutableStateListOf<Ami>() }

    fun navigateToConversation(userId: String) {
        navController.navigate("Conversation/$userId") {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }


    LaunchedEffect(Unit) {
        val tempList = mutableListOf<String>()

        db.collection("Amis")
            .whereEqualTo("user1", currentUserId)
            .get()
            .addOnSuccessListener { result1 ->
                val user2List = result1.documents.mapNotNull { it.getString("user2") }

                for (user2 in user2List) {
                    db.collection("Amis")
                        .whereEqualTo("user1", user2)
                        .whereEqualTo("user2", currentUserId)
                        .get()
                        .addOnSuccessListener { result2 ->
                            if (!result2.isEmpty && !tempList.contains(user2)) {
                                db.collection("Utilisateur")
                                    .document(user2)
                                    .get()
                                    .addOnSuccessListener { userDoc ->
                                        val username = userDoc.getString("username")
                                        if (username != null) {
                                            mutualFriends.add(Ami(id = user2, username = username))
                                            tempList.add(user2)
                                        }
                                    }
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Erreur de récupération des amis", Toast.LENGTH_SHORT).show()
            }
    }

    val filteredList = mutualFriends.filter {
        it.username.contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
                onClick = { /* nouvelle conversation */ },
                colors = ButtonDefaults.buttonColors(containerColor = grey)
            ) {
                Text("+", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            filteredList.forEach { ami ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            navigateToConversation(ami.id)
                        },
                    colors = CardDefaults.cardColors(containerColor = grey)
                ) {
                    Text(
                        text = ami.username,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

data class Message(
    val content: String,
    val isSentByCurrentUser: Boolean,
    val timestamp: Timestamp
)

@Composable
fun ConversationScreen(
    userId: String,           // ID de l'utilisateur cible (ami)
    currentUserId: String     // ID de l'utilisateur connecté (celui qui envoie les messages)
) {
    val context = LocalContext.current
    val messages = remember { mutableStateListOf<Message>() }
    val firestore = FirebaseFirestore.getInstance()

    val conversationId = listOf(currentUserId, userId).sorted().joinToString("_")

    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(conversationId) {
        firestore.collection("Messages")
            .whereEqualTo("conversationId", conversationId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(context, "Erreur Firestore : ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                messages.clear()
                snapshot?.documents?.forEach { doc ->
                    val text = doc.getString("text") ?: ""
                    val sender = doc.getString("sender") ?: ""
                    val timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()

                    messages.add(
                        Message(
                            content = text,
                            isSentByCurrentUser = sender == currentUserId,
                            timestamp = timestamp
                        )
                    )
                }
            }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = false
        ) {
            items(messages) { message ->
                val alignment = if (message.isSentByCurrentUser) Arrangement.End else Arrangement.Start
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = alignment) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = if (message.isSentByCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = message.content,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Écrire un message...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (messageText.isNotBlank()) {
                    val message = hashMapOf(
                        "text" to messageText.trim(),
                        "sender" to currentUserId,
                        "receiver" to userId,
                        "timestamp" to Timestamp.now(),
                        "conversationId" to conversationId
                    )
                    firestore.collection("Messages")
                        .add(message)
                        .addOnSuccessListener { messageText = "" }
                        .addOnFailureListener {
                            Toast.makeText(context, "Échec de l'envoi", Toast.LENGTH_SHORT).show()
                        }
                }
            }) {
                Text("Envoyer")
            }
        }
    }
}

@Composable
fun AddFriends(
    currentUserId: String,
    viewModel: FriendViewModel = viewModel()
) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val utilisateurs = remember { mutableStateListOf<Utilisateur>() }
    val followedUserIds = remember { mutableStateListOf<String>() }
    val coroutineScope = rememberCoroutineScope()

    // Fonction suspendue pour charger les amis et utilisateurs
    suspend fun loadFriendsAndUsers() {
        try {
            val amisResult = db.collection("Amis")
                .whereEqualTo("user1", currentUserId)
                .get()
                .await()

            followedUserIds.clear()
            for (doc in amisResult.documents) {
                doc.getString("user2")?.let { followedUserIds.add(it) }
            }

            val usersResult = db.collection("Utilisateur").get().await()
            utilisateurs.clear()
            for (doc in usersResult.documents) {
                val userId = doc.id
                val username = doc.getString("username")
                if (username != null && userId != currentUserId && !followedUserIds.contains(userId)) {
                    utilisateurs.add(Utilisateur(id = userId, username = username))
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur lors du chargement : ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Charge la liste au démarrage et quand currentUserId change
    LaunchedEffect(currentUserId) {
        loadFriendsAndUsers()
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(utilisateurs) { utilisateur ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = utilisateur.username, style = MaterialTheme.typography.bodyLarge)

                Button(
                    onClick = {
                        val data = hashMapOf(
                            "user1" to currentUserId,
                            "user2" to utilisateur.id
                        )
                        db.collection("Amis")
                            .add(data)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Ami ajouté !", Toast.LENGTH_SHORT).show()
                                // Recharge la liste dans une coroutine
                                coroutineScope.launch {
                                    loadFriendsAndUsers()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show()
                            }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text("Ajouter")
                }
            }
        }
    }
}
