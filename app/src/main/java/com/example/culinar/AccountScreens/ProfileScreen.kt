package com.example.culinar.AccountScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

@Composable
fun ProfileScreen(changeScreen: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "CRÉATION DE PROFIL", style = MaterialTheme.typography.titleLarge)

        var name by remember { mutableStateOf("") }

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom complet") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { changeScreen("login") },
               colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
            Text("Retour à la connexion")
        }
    }
}
