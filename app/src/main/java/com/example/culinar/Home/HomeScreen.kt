package com.example.culinar.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.culinar.models.Screen
import com.example.culinar.ui.theme.grey

// Top bar
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onAccountClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {

    var menuClicked by remember { mutableStateOf(false) }

    Column {
        Column(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary)
        ) {}

        Row(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primary),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Culinary",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 5.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = { menuClicked = !menuClicked },
                modifier = Modifier
                    .width(85.dp)
                    .height(85.dp)
                    .padding(0.dp),
                shape = CutCornerShape(3.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (menuClicked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Default.Face,
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.width(40.dp).height(40.dp)
                )
            }
        }

        if (menuClicked) {
            Column(
                modifier = Modifier
                    .background(color = grey)
                    .fillMaxWidth()
                    .height(180.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = {
                        onAccountClick()
                        menuClicked = false
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = CutCornerShape(3.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.AccountCircle,
                            contentDescription = "Account",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Compte", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                TextButton(
                    onClick = {
                        onSettingsClick()
                        menuClicked = false
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = CutCornerShape(3.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Paramètres", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                TextButton(
                    onClick = {
                        onLogoutClick()
                        menuClicked = false
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = CutCornerShape(3.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp).graphicsLayer { rotationZ = 180f }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Déconnexion", style = MaterialTheme.typography.bodyMedium)
                    }
                }

            }
        }
    }
}


// Bottom bar modifié avec username
@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    navRoutes: (String) -> Unit = {},
    navController: NavHostController,
    username: String?
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val screenName by remember {
        derivedStateOf { currentBackStackEntry?.destination?.route ?: Screen.Home.name }
    }

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    if (screenName == Screen.Calendar.name) Icons.Default.DateRange else Icons.Outlined.DateRange,
                    contentDescription = "Calendar",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            selected = screenName == Screen.Calendar.name,
            onClick = { navRoutes(Screen.Calendar.name) }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    if (screenName == Screen.Groceries.name) Icons.Default.ShoppingCart else Icons.Outlined.ShoppingCart,
                    contentDescription = "Groceries",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            selected = screenName == Screen.Groceries.name,
            onClick = { navRoutes(Screen.Groceries.name) }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    if (screenName.startsWith(Screen.Home.name)) Icons.Default.Home else Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            selected = screenName.startsWith(Screen.Home.name),
            onClick = {
                // Construire la route complète avec username si présent
                val route = if (!username.isNullOrEmpty()) {
                    "${Screen.Home.name}?username=$username"
                } else {
                    Screen.Home.name
                }
                navRoutes(route)
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    if (screenName == Screen.Recipes.name) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = "Recipes",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            selected = screenName == Screen.Recipes.name,
            onClick = { navRoutes(Screen.Recipes.name) }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    if (screenName == Screen.Community.name) Icons.Default.Email else Icons.Outlined.Email,
                    contentDescription = "Community",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            },
            selected = screenName == Screen.Community.name,
            onClick = { navRoutes(Screen.Community.name) }
        )
    }
}



@Composable
fun Home(
    navRoutes: (String) -> Unit,
    username: String?
) {
    val displayName = username.takeUnless { it.isNullOrEmpty() } ?: "Invité"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Utilisateur : $displayName",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = { navRoutes(Screen.PostFeed.name) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Poster sur mon feed")
            }

            Button(
                onClick = { navRoutes(Screen.CheckFeed.name) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Accéder à mon feed")
            }

            Button(
                onClick = { navRoutes(Screen.SendMessage.name) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Chatter avec des amis")
            }

            Button(
                onClick = { navRoutes(Screen.AddFriends.name) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Ajouter des amis")
            }
        }
    }
}



