package com.example.culinar.AccountScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.culinar.ui.theme.GreenPrimary

@Composable
fun SignupScreen(changeScreen: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "INSCRIPTION", style = MaterialTheme.typography.titleLarge)

        var email by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Nom d'utilisateur") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Mot de passe") }, visualTransformation = PasswordVisualTransformation())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { changeScreen("profile") },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary) ) {
            Text("Créer un profil")
        }
    }
}
