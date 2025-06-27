package com.example.culinar

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.culinar.AccountScreens.AccountScreen
import com.example.culinar.Home.Home // <-- importe ta fonction Home
import org.junit.Rule
import org.junit.Test

class AccountLoginTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScenarioAndNavigateToHome() {
        var connected by mutableStateOf(false)
        var username by mutableStateOf("")

        composeTestRule.setContent {
            if (!connected) {
                AccountScreen(
                    authAndNavigation = { _, user ->
                        connected = true
                        username = user
                    },
                    showSnackbar = {}
                )
            } else {
                Home(
                    navRoutes = {},
                    username = username,
                    showSnackbar = {}
                )
            }
        }

        // Entrer le nom d’utilisateur
        composeTestRule.onNodeWithText("Nom d'utilisateur")
            .performTextInput("magali")

        // Entrer le mot de passe
        composeTestRule.onNodeWithText("Mot de passe")
            .performTextInput("1234")

        // Cliquer sur "Se connecter"
        composeTestRule.onNodeWithText("Se connecter")
            .performClick()

        // Attendre la mise à jour de l’état (connexion)
        composeTestRule.waitUntil(5_000) { connected }

        // Vérifier que la page Home est affichée avec le bon username
        composeTestRule.onNodeWithText("Utilisateur : magali").assertIsDisplayed()
    }
}
