package com.example.culinar.navigationGraph

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.culinar.AccountScreens.AccountScreen
import com.example.culinar.Home.*
import com.example.culinar.models.Screen
import com.example.culinar.models.viewModels.CommunityViewModel
import com.example.culinar.models.viewModels.FriendViewModel
import com.example.culinar.viewmodels.RecipeViewModel

@Composable
fun CulinarApp(
    modifier: Modifier = Modifier,
    friendViewModel: FriendViewModel = viewModel(),
    viewModelRecipes: RecipeViewModel = viewModel(),
    communityViewModel: CommunityViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    var username by remember { mutableStateOf<String?>(null) } // État username

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val navRoutes: (String, String?) -> Unit = { screen, user ->
        val destination = if (user != null) "$screen?username=$user" else screen
        navController.navigate(destination) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                onAccountClick = {
                    navRoutes(Screen.Account.name, null) // Naviguer vers Account
                },
                onLogoutClick = {
                    username = null
                    navRoutes(Screen.Home.name, null)    // Naviguer vers Home avec username null
                },
                onSettingsClick = {
                    navRoutes(Screen.Settings.name, username) // Naviguer vers Paramètres (à créer)
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                navRoutes = { screen -> navRoutes(screen, username) },
                navController = navController,
                username = username ?: ""
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Account.name) {
                AccountScreen(authAndNavigation = { newUser ->
                    username = newUser
                    navRoutes(Screen.Home.name, newUser)
                })
            }

            composable(
                route = "${Screen.Home.name}?username={username}",
                arguments = listOf(
                    navArgument("username") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val user = backStackEntry.arguments?.getString("username") ?: username

                Home(
                    navRoutes = { screen -> navRoutes(screen, user) },
                    username = user
                )
            }

            // Ajoute ici un écran Paramètres si besoin
            composable(route = Screen.Settings.name) {
                // TODO: Ton composable pour paramètres
            }

            // Autres écrans
            composable(route = Screen.Calendar.name) { /* ... */ }
            composable(route = Screen.Groceries.name) { /* ... */ }
            composable(route = Screen.Recipes.name) { /* ... */ }
            composable(route = Screen.Community.name) { /* ... */ }
        }
    }
}
