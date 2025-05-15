package com.example.culinar

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.culinar.AccountScreens.LoginScreen
import com.example.culinar.AccountScreens.ProfileScreen
import com.example.culinar.AccountScreens.SignupScreen
import com.example.culinar.GroceriesScreens.Grocery
import com.example.culinar.ui.theme.CulinarTheme
import com.example.culinar.ui.theme.Typography
import com.example.culinar.ui.theme.darkGreen
import com.example.culinar.ui.theme.grey
import com.example.culinar.ui.theme.mediumGreen


enum class Screen { Account, Home, Calendar, Groceries, Recipes, Community }


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CulinarTheme {
                    CulinarApp()
            }
        }
    }
}



@Composable
fun CulinarApp(
    modifier: Modifier = Modifier,
    //viewModel: CulinarViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {

    // Getting the current back stack entry as a State
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    // Getting the current route from the back stack entry
    val currentRoute = currentBackStackEntry?.destination?.route

    val navRoutes = { screen: Screen ->
        navController.navigate(screen.name) {
            // Optional: Configuring navigation behavior (e.g., pop up to a specific destination)
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            // Avoiding building up a large stack of the same destination
            launchSingleTop = true
            // Restoring state when reselecting a previously selected item
            restoreState = true
        }
    }

    Surface (modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.name,
            modifier = Modifier
        ) {

            composable(route = Screen.Home.name) {
                HomeScreen(navRoutes = navRoutes, currentRoute = currentRoute, toAuth = { navRoutes(Screen.Account) })
            }

            composable(route = Screen.Groceries.name) {
                Grocery(navRoutes = navRoutes, currentRoute = currentRoute, toAuth = { navRoutes(Screen.Account) })
            }

            composable(route = Screen.Recipes.name) {
               RecipesScreen(navRoutes = navRoutes, currentRoute = currentRoute, toAuth = { navRoutes(Screen.Account) })
            }

            composable(route = Screen.Account.name) {
                AccountScreen(authAndNavigation = { navRoutes(Screen.Home) })
            }

            composable(route = Screen.Community.name) {
                CommunityScreen(navRoutes = navRoutes, currentRoute = currentRoute, toAuth = { navRoutes(Screen.Account) })
            }

            composable(route = Screen.Calendar.name) {
                CalendarScreen(navRoutes = navRoutes, currentRoute = currentRoute, toAuth = { navRoutes(Screen.Account) })
            }


        }
    }

}



