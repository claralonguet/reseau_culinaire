package com.example.culinar.AccountScreens

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun SignupScreen(changeScreen: (String) -> Unit) {

    val context = LocalContext.current
    val db = Firebase.firestore

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "INSCRIPTION", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Nom d'utilisateur") })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                when {
                    email.isBlank() || username.isBlank() || password.isBlank() ->
                        Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()

                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                        Toast.makeText(context, "Adresse email invalide", Toast.LENGTH_SHORT).show()

                    else -> {
                        db.collection("Utilisateur")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener { emailResult ->
                                if (!emailResult.isEmpty) {
                                    Toast.makeText(context, "Adresse email déjà utilisée", Toast.LENGTH_SHORT).show()
                                } else {
                                    db.collection("Utilisateur")
                                        .whereEqualTo("username", username)
                                        .get()
                                        .addOnSuccessListener { usernameResult ->
                                            if (!usernameResult.isEmpty) {
                                                Toast.makeText(context, "Nom d'utilisateur déjà pris", Toast.LENGTH_SHORT).show()
                                            } else {
                                                val user = hashMapOf(
                                                    "email" to email,
                                                    "username" to username,
                                                    "password" to password, // Utiliser un hash sécurisé en production
                                                    "admin" to false,
                                                    "expert" to false
                                                )

                                                db.collection("Utilisateur")
                                                    .add(user)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(context, "Profil créé avec succès", Toast.LENGTH_SHORT).show()
                                                        changeScreen("profile")
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Log.w("SignupScreen", "Erreur lors de l'ajout", e)
                                                        Toast.makeText(context, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                        }
                                }
                            }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Créer un profil")
        }
    }
}
