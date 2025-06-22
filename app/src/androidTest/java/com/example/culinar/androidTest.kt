package com.example.culinar

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.culinar.AccountScreens.AccountScreen
import com.example.culinar.Home.Home // ta fonction Home
import com.example.culinar.ui.theme.CulinarTheme // Remplace par ton thème
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
            CulinarTheme { // Enveloppe dans ton thème personnalisé
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
                        username = username
                    )
                }
            }
        }

        // Étape 1 : Entrer le nom d’utilisateur
        composeTestRule.onNodeWithText("Nom d'utilisateur")
            .performTextInput("magali")

        // Étape 2 : Entrer le mot de passe
        composeTestRule.onNodeWithText("Mot de passe")
            .performTextInput("1234")

        // Étape 3 : Cliquer sur "Se connecter"
        composeTestRule.onNodeWithText("Se connecter")
            .performClick()

        // Attendre la mise à jour de l’état
        composeTestRule.waitUntil(5_000) { connected }

        // Vérifier que l'écran Home affiche bien le nom d’utilisateur avec les couleurs définies dans le thème
        composeTestRule.onNodeWithText("Utilisateur : magali").assertIsDisplayed()
    }
}
