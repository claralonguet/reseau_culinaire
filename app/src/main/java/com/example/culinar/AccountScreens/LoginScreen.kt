package com.example.culinar.AccountScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.culinar.ui.theme.CulinarTheme


@Composable
fun AccountScreen (modifier: Modifier = Modifier, authAndNavigation: () -> Unit = {}) {

    var currentScreen by remember { mutableStateOf("login") }

    Box()
    {
        when (currentScreen) {
            "login" -> LoginScreen ({ currentScreen = "signup" }, authAndNavigation = authAndNavigation)
            "signup" -> SignupScreen { currentScreen = "profile" }
            "profile" -> ProfileScreen { currentScreen = "login" }
        }
    }
}


@Composable
fun LoginScreen(
    changeScreen: (String) -> Unit,
    authAndNavigation: () -> Unit,
    // authViewModel: AuthViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "CONNEXION", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.tertiary)

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

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
            onClick = { authAndNavigation() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Se connecter")
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
