package com.example.culinar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(changeScreen: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "CONNEXION", style = MaterialTheme.typography.titleLarge)

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Nom d'utilisateur") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Mot de passe") }, visualTransformation = PasswordVisualTransformation())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /* Ajouter une action ici */ }) {
            Text("Se connecter")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { changeScreen("signup") }) {
            Text("S'inscrire")
        }
    }
}
