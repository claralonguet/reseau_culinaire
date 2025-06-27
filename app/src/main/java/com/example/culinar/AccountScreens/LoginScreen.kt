package com.example.culinar.AccountScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Écran principal de gestion de compte.
 * Il affiche soit l'écran de connexion, d'inscription ou de profil
 * en fonction de l'état interne `currentScreen`.
 */
@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    authAndNavigation: (userId: String, username: String) -> Unit = { _, _ -> },
    showSnackbar: (String) -> Unit
) {
    var currentScreen by remember { mutableStateOf("login") }

    Box {
        when (currentScreen) {
            "login" -> LoginScreen(
                changeScreen = { currentScreen = "signup" },
                authAndNavigation = authAndNavigation,
                showSnackbar = showSnackbar
            )
            "signup" -> SignupScreen { currentScreen = "profile" }
            "profile" -> ProfileScreen { currentScreen = "login" }
        }
    }
}

/**
 * Écran de connexion utilisateur.
 * Permet à l'utilisateur de saisir son nom d'utilisateur et mot de passe.
 * Vérifie les informations dans Firestore et redirige si la connexion réussit.
 */

@Composable
fun LoginScreen(
    changeScreen: (String) -> Unit,
    authAndNavigation: (String, String) -> Unit, // id, username
    showSnackbar: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CONNEXION",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.tertiary
        )

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }
        val db = Firebase.firestore

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nom d'utilisateur") },
            shape = MaterialTheme.shapes.medium
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { // verification des differents champs
                errorMessage = "" // reset erreur
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "Veuillez remplir tous les champs"
                    return@Button
                }

                db.collection("Utilisateur")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val userDoc = documents.documents[0]
                            val storedPassword = userDoc.getString("password")

                            if (storedPassword == password) {
                                val userId = userDoc.id
                                authAndNavigation(userId, username)
                            } else {
                                errorMessage = "Mot de passe incorrect"
                            }
                        } else {
                            errorMessage = "Nom d'utilisateur introuvable"
                        }
                    }
                    .addOnFailureListener { exception ->
                        errorMessage = "Erreur lors de la connexion : ${exception.message}"
                    }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Se connecter")
        }

        if (errorMessage.isNotEmpty()) {
            //Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            showSnackbar(errorMessage)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { changeScreen("signup") },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
            shape = MaterialTheme.shapes.large
        ) {
            Text("S'inscrire")
        }
    }
}
