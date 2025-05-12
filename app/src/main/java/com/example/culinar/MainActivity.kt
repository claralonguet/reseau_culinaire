package com.example.culinar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.culinar.ui.theme.CulinarTheme
import com.example.culinar.ui.theme.Typography
import com.example.culinar.ui.theme.darkGreen
import com.example.culinar.ui.theme.grey
import com.example.culinar.ui.theme.mediumGreen


enum class Screen { Account, Home, Calendar, Groceries, Recipes, Community }


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

    // Get the current back stack entry as a State
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    // Get the current route from the back stack entry
    val currentRoute = currentBackStackEntry?.destination?.route

    val navRoutes = { screen: Screen ->
        navController.navigate(screen.name) {
            // Optional: Configure navigation behavior (e.g., pop up to a specific destination)
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            // Avoid building up a large stack of the same destination
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
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
                HomeScreen(navRoutes = navRoutes, currentRoute = currentRoute)
            }

            composable(route = Screen.Groceries.name) {
                Grocery(navRoutes = navRoutes, currentRoute = currentRoute)
            }

            composable(route = Screen.Recipes.name) {
               RecipesScreen(navRoutes = navRoutes, currentRoute = currentRoute)
            }

            composable(route = Screen.Account.name) {
                AccountScreen()
            }

            composable(route = Screen.Community.name) {
                CommunityScreen(navRoutes = navRoutes, currentRoute = currentRoute)
            }

            composable(route = Screen.Calendar.name) {
                CalendarScreen(navRoutes = navRoutes, currentRoute = currentRoute)
            }


        }
    }

}



@Composable
fun HomeScreen (modifier: Modifier = Modifier, navRoutes: (Screen) -> Unit = {}, currentRoute: String? = null) {
    Box()
    {
        Header(modifier = modifier)
        Footer(modifier = modifier, navRoutes = navRoutes, screenName = currentRoute ?: "home")
    }
}

@Composable
fun AccountScreen (modifier: Modifier = Modifier) {
    Box()
    {
        Header(modifier = modifier)
    }
}

@Composable
fun CalendarScreen (modifier: Modifier = Modifier, navRoutes: (Screen) -> Unit = {}, currentRoute: String? = null) {
    Box()
    {
        Header(modifier = modifier)
        Footer(modifier = modifier, screenName = currentRoute ?: "calendar", navRoutes = navRoutes)
    }
}

@Composable
fun RecipesScreen (modifier: Modifier = Modifier, navRoutes: (Screen) -> Unit = {}, currentRoute: String? = null) {
    Box()
    {
        Header(modifier = modifier)
        Footer(modifier = modifier, screenName = currentRoute ?: "recipes", navRoutes = navRoutes)
    }
}

@Composable
fun CommunityScreen (modifier: Modifier = Modifier, navRoutes: (Screen) -> Unit = {}, currentRoute: String? = null) {
    Box()
    {
        Header(modifier = modifier)
        Footer(modifier = modifier, screenName = currentRoute ?: "community", navRoutes = navRoutes)
    }
}


// Header bar

@Composable
fun Header(modifier: Modifier = Modifier, screenName: String = "home") {

    var menuClicked by remember { mutableStateOf(false) }

    Column {
        Column(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
                .background(color = mediumGreen)
        ){}


        Row(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .background(color = mediumGreen),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // App title
            Text(
                text = "Culinary",
                style = Typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 5.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            /*
            Text(
                text = "Esteban GOMEZ",
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                lineHeight = 21.sp,
                modifier = Modifier.width(120.dp),
            )
            */
            //Spacer(modifier = Modifier.weight(1f))
            // Profile picture
            TextButton(
                onClick = {menuClicked = !menuClicked},
                modifier = Modifier.width(85.dp).height(85.dp).padding(0.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonColors(
                    containerColor = if (menuClicked) darkGreen else mediumGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Face,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier.width(40.dp).height(40.dp)
                )
            }
        }

        // Action bar with profile and settings options
        if (menuClicked) {
            Column(
                modifier = Modifier
                    .background(color = grey)
                    .fillMaxWidth()
                    .height(180.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {

                // Profile settings button
                TextButton(
                    onClick = { menuClicked = !menuClicked },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = CutCornerShape(3.dp),
                    colors = ButtonColors(
                        containerColor = Color(0x00000000),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0x00000000),
                        disabledContentColor = Color.White
                    )
                ) {

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.AccountCircle,
                            contentDescription = "Account",
                            tint = Color(0xFF0A0A0A),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                        )
                        Text("Compte", style = Typography.bodyMedium, color = Color(0xFF0A0A0A), modifier = Modifier.padding(horizontal = 10.dp))
                    }
                }

                // Settings button
                TextButton(
                    onClick = { menuClicked = !menuClicked },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = CutCornerShape(3.dp),
                    colors = ButtonColors(
                        containerColor = Color(0x00000000),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0x00000000),
                        disabledContentColor = Color.White
                    )
                ) {

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF0A0A0A),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                        )
                        Text("Paramètres", style = Typography.bodyMedium, color = Color(0xFF0A0A0A), modifier = Modifier.padding(horizontal = 10.dp))
                    }
                }

                // Log out button
                TextButton (
                    onClick = { menuClicked = !menuClicked },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = CutCornerShape(3.dp),
                    colors = ButtonColors(
                        containerColor = Color(0x00000000),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0x00000000),
                        disabledContentColor = Color.White
                    )
                ) {

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color(0xFF0A0A0A),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .graphicsLayer {
                                    rotationZ = 180f
                                }
                        )
                        Text("Déconnexion", style = Typography.bodyMedium, color = Color(0xFF0A0A0A), modifier = Modifier.padding(horizontal = 10.dp))
                    }
                }
            }
        }
    }
}

// Footer bar

@Composable
fun Footer(modifier: Modifier = Modifier, screenName: String = "home", navRoutes: (Screen) -> Unit = {}) {

    Column {
        Spacer(modifier = Modifier.weight(1f))

        // Footer bar
        Row(
            modifier = Modifier
                .height(90.dp)
                .fillMaxWidth()
                .background(color = mediumGreen),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Calendar button
            Log.d("Footer", screenName)
            Button(
                onClick = { navRoutes(Screen.Calendar) },
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = if(screenName == Screen.Calendar.name) darkGreen else mediumGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.width(40.dp).height(40.dp)
                )

            }

            // Groceries button
            Button(
                onClick = { navRoutes(Screen.Groceries) },
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = if(screenName == Screen.Groceries.name) darkGreen else mediumGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }


            // Home button
            Button(
                onClick = { navRoutes(Screen.Home) },
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = if(screenName == Screen.Home.name) darkGreen else mediumGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }


            // Recipes button
            Button(
                onClick = { navRoutes(Screen.Recipes) },
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = if(screenName == Screen.Recipes.name) darkGreen else mediumGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }


            // Community button
            Button(
                onClick = { navRoutes(Screen.Community) },
                modifier = Modifier.weight(1f).height(72.dp).padding(0.dp),
                shape = CutCornerShape(1.dp),
                colors = ButtonColors(
                    containerColor = if(screenName == Screen.Community.name) darkGreen else mediumGreen,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF3CB460),
                    disabledContentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = "Clear",
                    tint = Color.White,
                    modifier = Modifier.width(45.dp).height(45.dp)
                )
            }

            //

        }
    }
}
