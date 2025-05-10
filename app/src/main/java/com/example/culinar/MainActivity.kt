package com.example.culinar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.culinar.LoginScreen
import com.example.culinar.SignupScreen
import com.example.culinar.ProfileScreen
import com.example.culinar.ui.theme.CulinarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CulinarTheme {
                var currentScreen by remember { mutableStateOf("login") }

                when (currentScreen) {
                    "login" -> LoginScreen { currentScreen = "signup" }
                    "signup" -> SignupScreen { currentScreen = "profile" }
                    "profile" -> ProfileScreen { currentScreen = "login" }
                }
            }
        }
    }
}
