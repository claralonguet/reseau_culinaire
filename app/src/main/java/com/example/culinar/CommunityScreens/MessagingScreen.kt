package com.example.culinar.CommunityScreens

import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.culinar.models.FriendViewModel
import com.example.culinar.ui.theme.grey


@Composable
fun SendMessage(navController: NavController, viewModel: FriendViewModel = viewModel()) {
    var searchText by remember { mutableStateOf("") }

    val filteredList = viewModel.friends.filter { friend :String ->
        friend.contains(searchText, ignoreCase = true)
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
fun ConversationScreen(username: String?) {
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

